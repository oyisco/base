package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String code);
}
