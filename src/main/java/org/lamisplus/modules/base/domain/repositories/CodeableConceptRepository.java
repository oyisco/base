package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.CodeableConcept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeableConceptRepository extends JpaRepository<CodeableConcept, Long> {
}
