package org.lamisplus.modules.base.web.rest;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Country;
import org.lamisplus.modules.base.domain.repositories.CodifierRepository;
import org.lamisplus.modules.base.domain.repositories.CountryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Country.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class CountryResource {

    private static final String ENTITY_NAME = "country";

    private final CountryRepository countryRepository;
    private final CodifierRepository codifierRepository;

    public CountryResource(CountryRepository countryRepository, CodifierRepository codifierRepository) {
        this.countryRepository = countryRepository;
        this.codifierRepository = codifierRepository;
        LOG.info("Codifier repository: {}", codifierRepository);
    }

    /**
     * GET  /countries : get all the countries.
     *
     * @return the ResponseEntity with countries 200 (OK) and the list of countries in body
     */
    @GetMapping("/countries")
    @Timed
    public List<Country> getAllCountries() {
        LOG.debug("REST request to get all Countries");
        return countryRepository.findAll();
    }
}
