package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class CodeableConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "concept_codifier",
            joinColumns = @JoinColumn(name = "concept_id"),
            inverseJoinColumns = @JoinColumn(name = "codifier_id"))
    private Collection<Codifier> codifiers;

    @NotNull
    private String text;
}
