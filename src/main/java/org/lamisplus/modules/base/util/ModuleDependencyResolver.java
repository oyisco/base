package org.lamisplus.modules.base.util;

import org.lamisplus.modules.base.config.ContextProvider;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.ModuleDependency;
import org.lamisplus.modules.base.domain.repositories.ModuleDependencyRepository;

import java.util.List;

public class ModuleDependencyResolver {
    private static ModuleDependencyRepository repository = ContextProvider.getBean(ModuleDependencyRepository.class);

    public static void resolveDependencies(Module module, List<Module> resolved, List<Module> unresolved)
            throws CyclicDependencyException, UnsatisfiedDependencyException {
        unresolved.add(module);
        for (ModuleDependency moduleDependency : repository.findDependencies(module)) {
            Module dependency = moduleDependency.getDependency();
            if (!versionSatisfied(moduleDependency)) {
                throw new UnsatisfiedDependencyException(
                        String.format("%s version requirements cannot be satisfied for module %s: " +
                                        "requires %s but %s installed", dependency.getName(), module.getName(),
                                module.getVersion(), dependency.getVersion()));
            }
            if (!resolved.contains(dependency)) {
                if (unresolved.contains(dependency)) {
                    throw new CyclicDependencyException(
                            String.format("Circular reference detected: %s -> %s", module.getName(), dependency.getName()));
                }
                resolveDependencies(dependency, resolved, unresolved);
            }
        }
        resolved.add(module);
        unresolved.remove(module);
    }

    private static boolean versionSatisfied(ModuleDependency moduleDependency) {
        String requiredVersion = moduleDependency.getVersion();
        String installedVersion = moduleDependency.getDependency().getVersion();
        String requiredMajor = requiredVersion.split("\\.")[0];
        String installedMajor = installedVersion.split("\\.")[0];

        return requiredMajor.equals(installedMajor);
    }
}
