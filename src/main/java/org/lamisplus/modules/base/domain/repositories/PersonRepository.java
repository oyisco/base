package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("select p from Person p where p <> ?1")
    List<Person> findAllExcept(Person person, Pageable pageable);

    @Query(value = "select * from person p where p.first_name ilike '%:name%' or p.last_name = '%:name%' " +
            "or p.other_names ilike '%:name%'", nativeQuery = true)
    List<Person> findByName(@Param("name") String name);
}
