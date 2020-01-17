package org.lamisplus.modules.base.service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foreach.across.core.annotations.Exposed;
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToMany;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@Exposed
public class ElasticsearchIndexService {

    private static final Lock reindexLock = new ReentrantLock();

    private final JestElasticsearchTemplate searchTemplate;

    public ElasticsearchIndexService(ElasticsearchOperations searchTemplate) {
        this.searchTemplate = (JestElasticsearchTemplate) searchTemplate;
    }

    @Async
    @Timed
    public <T, ID extends Serializable> void reindexAllAsync(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                             ElasticsearchRepository<T, ID> elasticsearchRepository) {
        reindexAll(entityClass, jpaRepository, elasticsearchRepository);
    }

    @Timed
    public <T, ID extends Serializable> void reindexAll(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                        ElasticsearchRepository<T, ID> elasticsearchRepository) {
        if (reindexLock.tryLock()) {
            try {
                reindexForClass(entityClass, jpaRepository, elasticsearchRepository);

                LOG.info("Elasticsearch: Successfully performed reindexing");
            } finally {
                reindexLock.unlock();
            }
        } else {
            LOG.info("Elasticsearch: concurrent reindexing attempt");
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        searchTemplate.deleteIndex(entityClass);
        try {
            searchTemplate.createIndex(entityClass);
        } catch (Exception e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        searchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            // if a JHipster entity field is the owner side of a many-to-many relationship, it should be loaded manually
            List<Method> relationshipGetters = Arrays.stream(entityClass.getDeclaredFields())
                    .filter(field -> field.getType().equals(Set.class))
                    .filter(field -> field.getAnnotation(ManyToMany.class) != null)
                    .filter(field -> field.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
                    .filter(field -> field.getAnnotation(JsonIgnore.class) == null)
                    .map(field -> {
                        try {
                            return new PropertyDescriptor(field.getName(), entityClass).getReadMethod();
                        } catch (IntrospectionException e) {
                            LOG.error("Error retrieving getter for class {}, field {}. Field will NOT be indexed",
                                    entityClass.getSimpleName(), field.getName(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            int size = 5000;
            for (int i = 0; i <= jpaRepository.count() / size; i++) {
                Pageable page = PageRequest.of(i, size);
                LOG.info("Indexing page {} of {}, size {}", i, jpaRepository.count() / size, size);
                Page<T> results = jpaRepository.findAll(page);
                results.map(result -> {
                    // if there are any relationships to load, do it now
                    relationshipGetters.forEach(method -> {
                        try {
                            // eagerly load the relationship set
                            ((Set) method.invoke(result)).size();
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage());
                        }
                    });
                    return result;
                });
                LOG.info("Indexing...");
                elasticsearchRepository.saveAll(results.getContent());
                LOG.info("Indexed");
            }
        }
        LOG.info("Elasticsearch: Indexed all rows for {}", entityClass.getSimpleName());
    }
}
