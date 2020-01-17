package org.lamisplus.modules.base.domain.entities;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@Table(name = "module_dependencies")
@ToString(of = {"id", "version"})
public class ModuleDependency implements Serializable, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @NotNull
    private Module module;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "dependency_id", nullable = false)
    private Module dependency;

    @Column(name = "version", nullable = false)
    @NotNull
    private String version;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
