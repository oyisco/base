package org.lamisplus.modules.base.web.rest;

import org.lamisplus.modules.base.domain.entities.Person;
import org.lamisplus.modules.base.domain.entities.PersonContact;
import org.lamisplus.modules.base.domain.repositories.PersonContactRepository;
import org.lamisplus.modules.base.domain.repositories.PersonRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PersonContactResource {
    private final PersonContactRepository personContactRepository;
    private final PersonRepository personRepository;

    public PersonContactResource(PersonContactRepository personContactRepository, PersonRepository personRepository) {
        this.personContactRepository = personContactRepository;
        this.personRepository = personRepository;
    }

    /**
     * GET  /person-contacts/person/:id : get the "id" person contacts.
     *
     * @param id the id of the person to retrieve contacts
     * @return the ResponseEntity with contacts 200 (OK) and with body the list of contacts for the person
     */
    @GetMapping("/person-contacts/person/{id}")
    public List<PersonContact> getContacts(@PathVariable Long id) {
        Optional<Person> person = personRepository.findById(id);
        return person.map(personContactRepository::findByPerson)
                .orElse(new ArrayList<>());
    }
}
