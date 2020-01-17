package org.lamisplus.modules.base.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MenuType {
    LINK("link"), DROP_DOWN("dropDown"), ICON("icon"), SEPARATOR("separator"), EXT_LINK("extLink");
    String type;

    MenuType(String type){
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
