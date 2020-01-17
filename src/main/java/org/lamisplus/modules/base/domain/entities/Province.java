package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "province")
@EqualsAndHashCode(of = "id")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "province_name")
    private String name;

    @JoinColumn(name = "state_id")
    @ManyToOne
    private State state;

    @Basic
    @Column(name = "archive")
    private Boolean archive = Boolean.FALSE;

}
