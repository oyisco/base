package org.lamisplus.modules.base.web.rest;

import org.lamisplus.modules.base.domain.entities.Person;
import org.lamisplus.modules.base.domain.repositories.PersonRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PersonResource {
    private final PersonRepository personRepository;

    public PersonResource(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/persons/except/{id}")
    public List<Person> getPersons(@PathVariable Long id, @RequestParam(value = "limit", defaultValue = "10") int limit,
                                    @RequestParam(name = "skip", defaultValue = "0") int skip) {
        Optional<Person> person = personRepository.findById(id);
        Pageable pageable = PageRequest.of(skip, limit);
        return person.map(person1 -> personRepository.findAllExcept(person1, pageable))
                .orElse(personRepository.findAll(pageable).getContent());
    }
}
