package org.lamisplus.modules.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lamisplus.modules.base.domain.enumeration.ModuleType;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "web_module")
@EqualsAndHashCode(of = {"id", "name"})
public final class WebModule implements Persistable<Long>, Serializable, Comparable<WebModule> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    private String path;

    private String title;

    private String breadcrumb;

    private Integer position;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ModuleType type;

    @ManyToOne
    @JsonIgnore
    private Module module;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "web_module_authorities",
            joinColumns = @JoinColumn(name = "web_module_id"))
    private Set<String> authorities = new HashSet<>();

    @JoinColumn(name = "provides_for_id")
    @ManyToOne
    @JsonIgnore
    private WebModule providesFor;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public int compareTo(WebModule o) {
        return position.compareTo(o.position);
    }
}
