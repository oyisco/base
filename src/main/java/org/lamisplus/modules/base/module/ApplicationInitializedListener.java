package org.lamisplus.modules.base.module;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.util.CyclicDependencyException;
import org.lamisplus.modules.base.util.ModuleDependencyResolver;
import org.lamisplus.modules.base.util.UnsatisfiedDependencyException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ApplicationInitializedListener {
    private final ModuleRepository moduleRepository;
    private final ModuleManager moduleManager;

    public ApplicationInitializedListener(ModuleRepository moduleRepository,
                                          ModuleManager moduleManager) {
        this.moduleRepository = moduleRepository;
        this.moduleManager = moduleManager;
    }

    @EventListener
    @Async
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOG.info("Scanning for active modules...");

        List<Module> modules = moduleRepository.findByActiveIsTrue();
        List<Module> resolved = new ArrayList<>();
        List<Module> unresolved = new ArrayList<>();
        List<Module> started = new ArrayList<>();

        modules.forEach(module -> {
            try {
                ModuleDependencyResolver.resolveDependencies(module, resolved, unresolved);
            } catch (CyclicDependencyException | UnsatisfiedDependencyException e) {
                LOG.error(e.getMessage());
            }
        });
        if (!resolved.isEmpty()) {
            LOG.debug("Starting up active modules ...");
        }
        resolved.forEach(module -> {
            if (!started.contains(module)) {
                started.add(module);
                try {
                    moduleManager.installModule(module, false);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
