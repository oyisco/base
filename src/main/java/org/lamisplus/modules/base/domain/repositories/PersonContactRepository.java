package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Person;
import org.lamisplus.modules.base.domain.entities.PersonContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonContactRepository extends JpaRepository<PersonContact, Long> {
    List<PersonContact> findByPerson(Person person);
}
