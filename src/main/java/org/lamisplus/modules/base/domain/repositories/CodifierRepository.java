package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Codifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodifierRepository extends JpaRepository<Codifier, Long> {

    List<Codifier> findByCodifierGroup(String group);
}
