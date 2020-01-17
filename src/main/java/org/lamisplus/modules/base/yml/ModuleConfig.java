package org.lamisplus.modules.base.yml;

import lombok.Data;
import org.lamisplus.modules.base.domain.entities.Authority;
import org.lamisplus.modules.base.domain.entities.Menu;

import java.util.*;

@Data
public class ModuleConfig {
    private String name;
    private String basePackage;
    private String umdLocation;
    private String moduleMap;
    private String version;
    private String resourceKey = "";
    private Map<String, String> dependencies = new HashMap<>();
    private List<WebModuleConfig> webModules = new ArrayList<>();
    private List<Authority> bundledAuthorities = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();
    private List<JsonForm> jsonForms = new ArrayList<>();
    private List<ComponentForm> componentForms = new ArrayList<>();
}
