package org.lamisplus.modules.base.yml;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WebModuleConfig {
    private String name;
    private String path;
    private String title = "";
    private String breadcrumb = "";
    private Integer position = 99;
    private List<String> authorities = new ArrayList<>();
}
