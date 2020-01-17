package org.lamisplus.modules.base.web.rest;

import org.lamisplus.modules.base.domain.entities.Person;
import org.lamisplus.modules.base.domain.entities.RelatedPerson;
import org.lamisplus.modules.base.domain.repositories.PersonRepository;
import org.lamisplus.modules.base.domain.repositories.RelatedPersonRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RelatedPersonResource {
    private final PersonRepository personRepository;
    private final RelatedPersonRepository relatedPersonRepository;

    public RelatedPersonResource(PersonRepository personRepository, RelatedPersonRepository relatedPersonRepository) {
        this.personRepository = personRepository;
        this.relatedPersonRepository = relatedPersonRepository;
    }

    /**
     * GET  /related-persons/person/:id : get the "id" person related persons
     *
     * @param id the id of the person to retrieve related persons
     * @return the ResponseEntity with related persons 200 (OK) and with body the list of related persons for the person
     */
    @GetMapping("/related-persons/person/{id}")
    public List<RelatedPerson> getContacts(@PathVariable Long id) {
        Optional<Person> person = personRepository.findById(id);
        return person.map(relatedPersonRepository::findByPerson)
                .orElse(new ArrayList<>());
    }
}
