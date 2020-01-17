package org.lamisplus.modules.base.module;

import com.foreach.across.core.AcrossContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
class ChildContextsHolder {
    private Map<String, AcrossContext> contexts = new HashMap<>();

    Map<String, AcrossContext> getContexts() {
        return contexts;
    }
}
