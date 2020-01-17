package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Form;
import org.lamisplus.modules.base.domain.repositories.FormRepository;
import org.lamisplus.modules.base.web.errors.BadRequestAlertException;
import org.lamisplus.modules.base.web.util.HeaderUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
public class FormResource {
    private static final String ENTITY_NAME = "form";

    private final FormRepository formRepository;

    public FormResource(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    /**
     * POST  /jsonForms : Create a new form.
     *
     * @param form the form to create
     * @return the ResponseEntity with status 201 (Created) and with body the new form, or with status 400 (Bad Request) if the form has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/forms")
    public ResponseEntity<Form> createAppointment(@RequestBody Form form) throws URISyntaxException {
        LOG.debug("REST request to save form : {}", form);
        if (form.getId() != null) {
            throw new BadRequestAlertException("A new form cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Form result = formRepository.save(form);
        return ResponseEntity.created(new URI("/api/jsonForms/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId()))
                .body(result);
    }

    /**
     * PUT  /jsonForms : Updates an existing form.
     *
     * @param form the form to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated form,
     * or with status 400 (Bad Request) if the form is not valid,
     * or with status 500 (Internal Server Error) if the form couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/forms")
    public ResponseEntity<Form> updateAppointment(@RequestBody Form form) throws URISyntaxException {
        LOG.debug("REST request to update form : {}", form);
        if (form.getId() == null) {
            throw new BadRequestAlertException("Invalid name", ENTITY_NAME, "id null");
        }
        Form result = formRepository.save(form);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, form.getId()))
                .body(result);
    }

    /**
     * GET  /jsonForms : get all the jsonForms.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of jsonForms in body
     */
    @GetMapping("/forms")
    public List<Form> getForms() {
        return formRepository.findByModule_ActiveTrue()
                .stream()
                .sorted(Comparator.comparing(Form::getName)
                        .thenComparing(Form::getPriority).reversed())
                .collect(Collectors.toList());
    }

    /**
     * GET  /jsonForms/:id : get the "id" form.
     *
     * @param id the name of the form to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the form, or with status 404 (Not Found)
     */
    @GetMapping("/forms/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable String id) {
        LOG.debug("REST request to get form : {}", id);
        Optional<Form> form = formRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(form);
    }

    /**
     * GET  /jsonForms/:name : get the "name" form.
     *
     * @param name the name of the form to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the form, or with status 404 (Not Found)
     */
    @GetMapping("/forms/{name}")
    public ResponseEntity<Form> getFormByName(@PathVariable String name) {
        LOG.debug("REST request to get form : {}", name);
        List<Form> forms = formRepository.findByName(name, PageRequest.of(0, 1));
        return ResponseUtil.wrapOrNotFound(forms.stream().findFirst());
    }
}
