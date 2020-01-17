package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Menu;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.enumeration.MenuLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByModuleAndLevel(Module module, MenuLevel level);

    List<Menu> findByLevelAndParentName(MenuLevel level, String name);

}
