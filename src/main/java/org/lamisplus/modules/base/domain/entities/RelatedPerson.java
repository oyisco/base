package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "related_person")
@EqualsAndHashCode(of = {"person", "related"})
public class RelatedPerson implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "person_id")
    @ManyToOne
    private Person person;

    @JoinColumn(name = "related_id")
    @ManyToOne
    private Person related;

    @JoinColumn(name = "relationship_type_id")
    @ManyToOne
    private Codifier relationshipType;
}
