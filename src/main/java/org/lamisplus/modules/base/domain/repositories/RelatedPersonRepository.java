package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Person;
import org.lamisplus.modules.base.domain.entities.RelatedPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelatedPersonRepository extends JpaRepository<RelatedPerson, Long> {
    List<RelatedPerson> findByPerson(Person person);
}
