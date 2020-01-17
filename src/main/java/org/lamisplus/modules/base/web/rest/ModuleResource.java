package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Menu;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.WebModule;
import org.lamisplus.modules.base.domain.enumeration.MenuLevel;
import org.lamisplus.modules.base.domain.enumeration.MenuType;
import org.lamisplus.modules.base.domain.repositories.MenuRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.domain.repositories.WebModuleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
public class ModuleResource {
    private final WebModuleRepository webModuleRepository;
    private final MenuRepository menuRepository;
    private final ModuleRepository moduleRepository;

    public ModuleResource(WebModuleRepository webModuleRepository,
                          MenuRepository menuRepository,
                          ModuleRepository moduleRepository) {
        this.webModuleRepository = webModuleRepository;
        this.menuRepository = menuRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping("/modules/{id:\\d+}/web-modules")
    @Timed
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<WebModule> getModulesByType(@PathVariable("id") Long id) {
        LOG.debug("Getting web modules for module: {}", id);

        Module module = moduleRepository.findById(id).orElse(null);
        if (module != null) {
            return webModuleRepository.findByModule(module);
        }
        return new ArrayList<>();
    }

    @GetMapping("/modules/{id:\\d+}")
    @Timed
    public ResponseEntity<Module> getModule(@PathVariable("id") Long id) {
        LOG.debug("Getting module: {}", id);

        Optional<Module> module = moduleRepository.findById(id);
        module = module.map(m -> {
            m.setWebModules(null);
            return m;
        });
        return ResponseUtil.wrapOrNotFound(module);
    }

    @GetMapping("/modules")
    @Timed
    public List<Module> getWebModules() {
        LOG.debug("Getting all active modules");

        List<Module> providers = new LinkedList<>(moduleRepository.findAllWithProviders());
        providers.addAll(moduleRepository.findAllWithoutProviders());
        providers = providers.stream()
                .map(module -> {
                    List<WebModule> webModules = webModuleRepository.findByModule(module);
                    module.setWebModules(new HashSet<>(webModules));
                    return module;
                }).collect(Collectors.toList());

        return providers;
    }

    @GetMapping("/modules/menus")
    public List<Menu> getMenus() {
        LOG.debug("Getting all menus for current user");

        List<Menu> menuItems = new ArrayList<>();
        Menu menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.ICON);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menuItems.add(menu);

        menu = new Menu();
        menu.setType(MenuType.SEPARATOR);
        menu.setName("Main Items");
        menuItems.add(menu);

        menu = new Menu();
        menu.setName("Dashboard");
        menu.setState("dashboard");
        menu.setType(MenuType.LINK);
        menu.setTooltip("Dashboard");
        menu.setIcon("dashboard");
        menuItems.add(menu);

        Menu admin = new Menu();
        admin.setName("Administration");
        admin.setState("admin");
        admin.setType(MenuType.DROP_DOWN);
        admin.setIcon("settings");
        admin.getAuthorities().add("ROLE_ADMIN");
        admin.setPosition(100);

        Menu mm = new Menu();
        mm.setName("Modules");
        mm.setPosition(10);
        mm.setState("modules");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("System Configuration");
        mm.setPosition(11);
        mm.setState("configuration");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("User Management");
        mm.setPosition(10);
        mm.setState("users");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Health Checks");
        mm.setPosition(12);
        mm.setState("health");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Application Metrics");
        mm.setPosition(13);
        mm.setState("metrics");
        admin.getSubs().add(mm);

        mm = new Menu();
        mm.setName("Log Configurations");
        mm.setPosition(14);
        mm.setState("logs");
        admin.getSubs().add(mm);

        List<Module> modules = moduleRepository.findByActiveIsTrue();
        modules.forEach(module -> {
            Set<Menu> menusL1 = new TreeSet<>(menuRepository.findByModuleAndLevel(module, MenuLevel.LEVEL_1));
            menusL1 = menusL1.stream()
                    .map(menu1 -> {
                        Set<Menu> menuL2 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_2, menu1.getName()));
                        menuL2 = menuL2.stream()
                                .map(menu2 -> {
                                    Set<Menu> menuL3 = new TreeSet<>(menuRepository.findByLevelAndParentName(MenuLevel.LEVEL_3, menu1.getName()));
                                    menu2.getSubs().addAll(menuL3);
                                    return menu2;
                                })
                                .collect(Collectors.toSet());
                        if (!menu1.getName().equals("Administration")) {
                            menu1.getSubs().addAll(menuL2);
                        } else {
                            List<Menu> adminSub = admin.getSubs();
                            adminSub.addAll(menuL2);
                            Collections.sort(adminSub);
                            admin.setSubs(adminSub);
                        }
                        return menu1;
                    })
                    .filter(menu1 -> !menu1.getName().equals("Administration"))
                    .collect(Collectors.toSet());
            menuItems.addAll(menusL1);
        });
        menuItems.add(admin);
        return menuItems;
    }

    @GetMapping("/modules/installed")
    @Cacheable(cacheNames = "modules")
    public List<Module> getModules() {
        LOG.debug("Get all installed modules");
        return moduleRepository.findAll().stream()
                .map(module -> {
                    module.setWebModules(null);
                    return module;
                })
                .collect(Collectors.toList());
    }
}
