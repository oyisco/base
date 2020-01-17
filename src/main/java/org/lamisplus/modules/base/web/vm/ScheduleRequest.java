package org.lamisplus.modules.base.web.vm;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class ScheduleRequest {
    @NotNull
    private String jobClass;
    @NotNull
    private String cronExpression;
    private Map<String, Object> jobData = new HashMap<>();
}
