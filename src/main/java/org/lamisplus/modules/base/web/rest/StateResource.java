package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.domain.entities.Country;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.CountryRepository;
import org.lamisplus.modules.base.domain.repositories.StateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing State.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class StateResource {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    public StateResource(StateRepository stateRepository, CountryRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * GET  /states/country/:code : get all states by country :code (id or ISO code)
     *
     * @return the ResponseEntity with states 200 (OK) and the list of states in body
     */
    @GetMapping("/states/country/{code}")
    @Timed
    public List<State> getStatesByCountryISOCode(@PathVariable String code) {
        LOG.debug("REST request to get all States by country ISO Code: {}", code);

        Optional<Country> country;
        if (StringUtils.isNumeric(code)) {
            country = countryRepository.findById(Long.parseLong(code));
        } else {
            country = countryRepository.findByCode(code);
        }
        return country.map(stateRepository::findByCountry)
                .orElse(new ArrayList<>());
    }

    /**
     * GET  /states/:id : get the "id" link.
     *
     * @param id the id of the link to retrieve
     * @return the ResponseEntity with link 200 (OK) and with body the link, or with 404 (Not Found)
     */
    @GetMapping("/states/{id}")
    @Timed
    public ResponseEntity<State> getState(@PathVariable Long id) {
        LOG.debug("REST request to get State : {}", id);
        Optional<State> state = stateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(state);
    }
}
