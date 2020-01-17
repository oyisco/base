package org.lamisplus.modules.base.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.web.vm.ScheduleRequest;
import org.lamisplus.modules.base.web.vm.ScheduleResponse;
import org.lamisplus.modules.base.web.vm.SchedulerTrigger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/scheduler")
public class JobSchedulerResource {
    private final Scheduler scheduler;
    private List<Trigger> suspendedTriggers = new ArrayList<>();

    public JobSchedulerResource(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/schedule-job")
    public ResponseEntity<ScheduleResponse> scheduleJob(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        try {
            JobDataMap jobDataMap = new JobDataMap();
            Map<String, Object> jobData = scheduleRequest.getJobData();
            jobData.forEach(jobDataMap::put);
            JobDetail jobDetail = buildJobDetail(
                    (Class<? extends Job>) this.getClass().getClassLoader()
                            .loadClass(scheduleRequest.getJobClass()), jobDataMap);
            Trigger trigger = buildJobTrigger(jobDetail, scheduleRequest.getCronExpression());
            scheduler.scheduleJob(jobDetail, trigger);

            ScheduleResponse scheduleResponse = new ScheduleResponse(true, "Job Scheduled Successfully!");
            scheduleResponse.setJobId(jobDetail.getKey().getName());
            scheduleResponse.setJobGroup(jobDetail.getKey().getGroup());
            return ResponseEntity.ok(scheduleResponse);
        } catch (SchedulerException | ClassNotFoundException ex) {
            LOG.error("Error scheduling group: {}", ex.getMessage());

            ScheduleResponse scheduleEmailResponse = new ScheduleResponse(false,
                    "Error scheduling group. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleEmailResponse);
        }
    }

    @GetMapping("/jobs")
    public List<SchedulerTrigger> getJobs() throws SchedulerException {
        List<SchedulerTrigger> schedulerTriggers = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                //get group's trigger
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                triggers.forEach(trigger -> {
                    SchedulerTrigger schedulerTrigger = new SchedulerTrigger();
                    schedulerTrigger.setJobId(jobName);
                    schedulerTrigger.setJobGroup(jobGroup);
                    CronScheduleBuilder scheduleBuilder = (CronScheduleBuilder) trigger.getScheduleBuilder();
                    CronTrigger cronTrigger = (CronTrigger) scheduleBuilder.build();
                    schedulerTrigger.setCronExpression(cronTrigger.getCronExpression());
                    schedulerTrigger.setDescription(trigger.getDescription());
                    schedulerTrigger.setEndTime(trigger.getEndTime());
                    schedulerTrigger.setFinalFireTime(trigger.getFinalFireTime());
                    schedulerTrigger.setPreviousFireTime(trigger.getPreviousFireTime());
                    schedulerTrigger.setNextFireTime(trigger.getNextFireTime());
                    schedulerTrigger.setStartTime(trigger.getStartTime());
                    try {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        if (triggerState.equals(Trigger.TriggerState.PAUSED)) {
                            schedulerTrigger.setActive(false);
                        }
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                    schedulerTriggers.add(schedulerTrigger);
                });
            }
        }
        return schedulerTriggers;
    }

    @GetMapping("/suspend/{job}")
    public void suspendJob(@PathVariable String job) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKey.getName().equals(job)) {
                    scheduler.pauseJob(jobKey);
                }
            }
        }

    }

    @GetMapping("/resume/{job}")
    public void resumeJob(@PathVariable String job) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKey.getName().equals(job)) {
                    scheduler.resumeJob(jobKey);
                }
            }
        }
    }

    @DeleteMapping("/delete/{job}")
    public Boolean deleteJob(@PathVariable String job) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKey.getName().equals(job)) {
                    return scheduler.deleteJob(jobKey);
                }
            }
        }
        return false;
    }

    @DeleteMapping("/delete-group/{group}")
    public Boolean deleteJobGroup(@PathVariable String group) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            if (groupName.equals(group)) {
                List<JobKey> jobKeys = new ArrayList<>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
                return scheduler.deleteJobs(jobKeys);
            }
        }
        return false;
    }

    @GetMapping("/job-classes")
    public List<String> listJobClasses() {
        List<String> classes = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(QuartzJobBean.class));
        Set<BeanDefinition> beans = provider.findCandidateComponents("org.fhi360");
        for (BeanDefinition bd : beans) {
            classes.add(bd.getBeanClassName());
        }
        return classes;
    }

    private JobDetail buildJobDetail(Class<? extends Job> jobClass, JobDataMap jobDataMap) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(UUID.randomUUID().toString(), String.format("%s-jobs",
                        jobDataMap.get("description") != null ? jobDataMap.get("description") : jobClass.getName()))
                .withDescription(jobDataMap.getString("description"))
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, String cronExpression) {
        String name = jobDetail.getDescription() != null ? jobDetail.getDescription() : jobDetail.getJobClass().getName();
        String description = String.format("%s trigger", name);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), String.format("%s-triggers", name))
                .withDescription(description)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

    @Scheduled(cron = "0 */3 * ? * *")
    public void removeJobsInErrorState() throws SchedulerException {
        LOG.debug("Delete jobs in Error link");
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                triggers.forEach(trigger -> {
                    try {
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                        if (triggerState.equals(Trigger.TriggerState.ERROR)) {
                            scheduler.deleteJob(jobKey);
                        }
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    @Scheduled(cron = "0 */3 * ? * *")
    public void resumeSuspendedTriggers() {
        suspendedTriggers.forEach(trigger -> {
            try {
                scheduler.resumeJob(trigger.getJobKey());
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
        suspendedTriggers.clear();
    }

    @PostConstruct
    public void init() throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                //get group's trigger
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                triggers.forEach(trigger -> {
                    Date now = Calendar.getInstance().getTime();
                    if (trigger.getNextFireTime().before(now)) {
                        try {
                            scheduler.pauseTrigger(trigger.getKey());
                            suspendedTriggers.add(trigger);
                        } catch (SchedulerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
