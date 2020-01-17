package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Province;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.ProvinceRepository;
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
 * REST controller for managing Province.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ProvinceResource {

    private final ProvinceRepository provinceRepository;

    private final StateRepository stateRepository;

    public ProvinceResource(ProvinceRepository provinceRepository, StateRepository stateRepository) {
        this.provinceRepository = provinceRepository;
        this.stateRepository = stateRepository;
    }

    /**
     * GET  /provinces/link/stateId : get all the localAuthorities for link with id.
     *
     * @return the ResponseEntity with province 200 (OK) and the list of Province in body
     */
    @GetMapping("/provinces/state/{stateId}")
    @Timed
    public List<Province> getAllProvinceByStateId(@PathVariable Long stateId) {
        LOG.debug("REST request to get all Province for state: {}", stateId);

        Optional<State> state = stateRepository.findById(stateId);
        return state.map(provinceRepository::findByStateOrderByName)
                .orElse(new ArrayList<>());
    }

    /**
     * GET  /provinces/:id : get the "id" province.
     *
     * @param id the id of the province to retrieve
     * @return the ResponseEntity with provinces 200 (OK) and with body the list of provinces for the state
     */
    @GetMapping("/provinces/{id}")
    @Timed
    public ResponseEntity<Province> getProvinces(@PathVariable Long id) {
        LOG.debug("REST request to get Province : {}", id);
        Optional<Province> province = provinceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(province);
    }
}
