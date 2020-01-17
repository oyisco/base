package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repositories for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
