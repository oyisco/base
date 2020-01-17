package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
@Data
@ToString(of = {"street", "street2", "zip", "city"})
public class Address implements Serializable {

    @NotNull
    private String street;

    private String street2;

    @JoinColumn(name = "province_id")
    @ManyToOne
    private Province province;

    @Column(name = "zip_code")
    private String zip;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "city")
    private String city;

    @JoinColumn(name = "state_id")
    @ManyToOne
    private State state;

    @JoinColumn(name = "country_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;
}
