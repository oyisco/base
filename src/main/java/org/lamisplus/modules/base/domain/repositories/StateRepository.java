package org.lamisplus.modules.base.domain.repositories;

import com.foreach.across.core.annotations.Exposed;
import org.lamisplus.modules.base.domain.entities.Country;
import org.lamisplus.modules.base.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Spring Data JPA repositories for the State entity.
 */
@SuppressWarnings("unused")
@Exposed
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByCountry(Country country);
}
