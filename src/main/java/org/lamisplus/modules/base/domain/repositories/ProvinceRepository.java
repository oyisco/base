package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Province;
import org.lamisplus.modules.base.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findByStateOrderByName(State state);
}
