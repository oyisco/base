package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Authority;
import org.lamisplus.modules.base.domain.repositories.AuthorityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthorityResource {
    private final AuthorityRepository authorityRepository;

    public AuthorityResource(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    /**
     * GET  /authorities/:id : get the "id" authority.
     *
     * @param id the id of the link to retrieve
     * @return the ResponseEntity with authority 200 (OK) and with body the authority, or with  404 (Not Found)
     */
    @GetMapping("/authorities/{id}")
    @Timed
    public ResponseEntity<Authority> getState(@PathVariable String id) {
        LOG.debug("REST request to get Authority : {}", id);
        Optional<Authority> authority = authorityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(authority);
    }

}
