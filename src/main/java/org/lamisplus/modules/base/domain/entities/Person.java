package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Table(name = "person")
public class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    private String lastName;

    @Column(name = "other_names")
    private String otherNames;

    @JoinColumn(name = "title_id")
    @ManyToOne
    private Codifier title;

    @Basic
    @Column(name = "dob")
    private LocalDate dob;

    @Basic
    @Column(name = "dob_estimated")
    private Boolean dobEstimated;

    @JoinColumn(name = "gender_id")
    @ManyToOne
    private Codifier gender;

    @JoinColumn(name = "education_id")
    @ManyToOne
    private Codifier education;

    @JoinColumn(name = "occupation_id")
    @ManyToOne
    private Codifier occupation;

    @JoinColumn(name = "marital_status_id")
    @ManyToOne
    private Codifier maritalStatus;


    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "person")
    private List<PersonContact> personContact;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "person")
    private List<RelatedPerson> relatedPeople;

}
