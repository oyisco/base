package org.lamisplus.modules.base.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.domain.entities.*;
import org.lamisplus.modules.base.domain.enumeration.ModuleType;
import org.lamisplus.modules.base.domain.repositories.FormRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.util.UnsatisfiedDependencyException;
import org.lamisplus.modules.base.yml.ComponentForm;
import org.lamisplus.modules.base.yml.JsonForm;
import org.lamisplus.modules.base.yml.ModuleConfig;
import org.lamisplus.modules.base.yml.WebModuleConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ModuleConfigProcessor {

    private final ModuleRepository moduleRepository;
    private final FormRepository formRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ModuleConfigProcessor(ModuleRepository moduleRepository, FormRepository formRepository) {
        this.moduleRepository = moduleRepository;
        this.formRepository = formRepository;
    }

    @Transactional
    public void processModuleConfig(ModuleConfig moduleConfig, Module module) throws UnsatisfiedDependencyException {
        if (moduleConfig != null) {
            if (moduleConfig.getUmdLocation() != null) {
                module.setUmdLocation(String.format("across/resources/static/%s/js/%s",
                        StringUtils.isBlank(moduleConfig.getResourceKey()) ? moduleConfig.getName() :
                                moduleConfig.getResourceKey(), moduleConfig.getUmdLocation()));
                if (moduleConfig.getUmdLocation().startsWith("/")) {
                    module.setUmdLocation(moduleConfig.getUmdLocation());
                }
            }
            module.getBundledAuthorities().clear();
            Map<String, String> dependencies = moduleConfig.getDependencies();
            List<ModuleDependency> moduleDependencies = new ArrayList<>();
            dependencies.forEach((name, version) -> {
                if (!name.equals("BaseModule")) {
                    Optional<Module> m = moduleRepository.findByName(name);
                    if (!m.isPresent()) {
                        throw new UnsatisfiedDependencyException(
                                String.format("Unsatisfied module requirement: %s not installed.", name));
                    } else {
                        if (!versionSatisfied(m.get().getVersion(), version)) {
                            throw new UnsatisfiedDependencyException(
                                    String.format("Version requirements cannot be satisfied for module: %s required, %s installed. ",
                                            version, m.get().getVersion()));
                        }
                        ModuleDependency moduleDependency = new ModuleDependency();
                        moduleDependency.setDependency(module);
                        moduleDependency.setDependency(m.get());
                        moduleDependency.setVersion(version);
                        moduleDependencies.add(moduleDependency);
                    }
                }
            });
            module.getDependencies().addAll(moduleDependencies);
            Set<Authority> authorities = moduleConfig.getBundledAuthorities().stream()
                    .map(authority -> {
                        authority.setModule(module);
                        return authority;
                    })
                    .collect(Collectors.toSet());
            module.setBundledAuthorities(authorities);
            module.getMenus().clear();
            Set<Menu> menus;
            menus = moduleConfig.getMenus().stream()
                    .map(menuItem -> {
                        menuItem.setModule(module);
                        return menuItem;
                    })
                    .map(menu -> {
                        List<Menu> subs1 = menu.getSubs();
                        subs1 = subs1.stream()
                                .map(sub -> {
                                    sub.setModule(menu.getModule());
                                    sub.setParent(menu);
                                    return sub;
                                })
                                .map(sub -> {
                                    List<Menu> subs2 = sub.getSubs();
                                    subs2 = subs2.stream()
                                            .map(sub2 -> {
                                                sub2.setModule(menu.getModule());
                                                sub2.setParent(sub);
                                                return sub2;
                                            })
                                            .collect(Collectors.toList());
                                    sub.getSubs().clear();
                                    sub.getSubs().addAll(subs2);
                                    return sub;
                                })
                                .collect(Collectors.toList());
                        menu.getSubs().clear();
                        menu.getSubs().addAll(subs1);
                        return menu;
                    })
                    .collect(Collectors.toSet());
            module.setMenus(menus);

            List<WebModuleConfig> webModuleConfigs = moduleConfig.getWebModules();
            for (WebModuleConfig webModuleConfig : webModuleConfigs) {
                WebModule webModule = readWebModuleProperties(module, webModuleConfig);
                module.getWebModules().add(webModule);
            }
            Module saveModule = moduleRepository.save(module);
            saveJsonForm(saveModule, moduleConfig);
            saveComponentForm(saveModule, moduleConfig);
        }
    }

    private WebModule readWebModuleProperties(Module module, WebModuleConfig config) {
        WebModule webModule = new WebModule();
        webModule.setModule(module);
        webModule.setName(config.getName());
        webModule.setTitle(config.getTitle());
        webModule.setBreadcrumb(config.getBreadcrumb());
        webModule.setPath(config.getPath());
        webModule.setPosition(config.getPosition());
        webModule.setType(ModuleType.WEB);
        return webModule;
    }

    private void saveJsonForm(Module module, ModuleConfig config) {
        List<JsonForm> forms = config.getJsonForms();
        forms.forEach(jsonForm -> {
            String name = jsonForm.getName();
            String path = jsonForm.getPath();
            Integer priority = jsonForm.getPriority();
            loadJsonForm(module.getName(), name, path, priority);
        });
    }

    private void saveComponentForm(Module module, ModuleConfig config) {
        List<ComponentForm> forms = config.getComponentForms();
        forms.forEach(componentForm -> {
            String name = componentForm.getName();
            String path = componentForm.getPath();
            Integer priority = componentForm.getPriority();
            loadComponentForm(module.getName(), name, path, priority);
        });
    }

    private boolean versionSatisfied(String installedVersion, String requiredVersion) {
        String requiredMajor = requiredVersion.split("\\.")[0];
        String installedMajor = installedVersion.split("\\.")[0];

        return requiredMajor.equals(installedMajor);
    }

    private void loadJsonForm(String moduleName, String templateName, String path, Integer priority) {
        moduleRepository.findByName(moduleName).ifPresent(module -> {
            List<Form> templates = formRepository.findByNameAndModule(templateName, module);
            if (templates.isEmpty()) {
                try {
                    JsonNode json = objectMapper.readTree(new ClassPathResource(path).getInputStream());
                    Form form = new Form();
                    form.setName(templateName);
                    form.setModule(module);
                    form.setForm(json);
                    form.setPriority(priority);
                    formRepository.save(form);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                templates.forEach(form -> {
                    try {
                        JsonNode template = objectMapper.readTree(new ClassPathResource(path).getInputStream());
                        form.setForm(template);
                        form.setPriority(priority);
                        formRepository.save(form);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void loadComponentForm(String moduleName, String templateName, String path, Integer priority) {
        moduleRepository.findByName(moduleName).ifPresent(module -> {
            List<Form> templates = formRepository.findByNameAndModule(templateName, module);
            if (templates.isEmpty()) {
                Form form = new Form();
                form.setName(templateName);
                form.setModule(module);
                form.setPath(path);
                form.setPriority(priority);
                formRepository.save(form);
            } else {
                templates.forEach(form -> {
                    form.setPath(path);
                    form.setPriority(priority);
                    formRepository.save(form);
                });
            }
        });
    }
}
