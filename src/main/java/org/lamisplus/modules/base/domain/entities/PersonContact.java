package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_contact")
@Data
@EqualsAndHashCode
@ToString
public class PersonContact implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "mobile_phone_number")
    private String mobilePhoneNumber;

    @Basic
    @Column(name = "alternate_phone_number")
    private String alternatePhoneNumber;

    @Basic
    @Column(name = "email")
    private String email;

    @Embedded
    private Address address;

    @JoinColumn(name = "person_id")
    @ManyToOne
    private Person person;
}
