package org.lamisplus.modules.base.web.vm;

import lombok.Data;

import java.util.Date;

@Data
public class SchedulerTrigger {
    private String jobId;
    private String jobGroup;
    private String cronExpression;
    private Boolean active = true;
    private String description;
    private Date startTime;
    private Date endTime;
    private Date finalFireTime;
    private Date nextFireTime;
    private Date previousFireTime;
}
