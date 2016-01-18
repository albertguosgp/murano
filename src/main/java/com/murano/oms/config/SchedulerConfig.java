package com.murano.oms.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.murano.oms.base.feature.FeatureService;
import com.murano.oms.base.service.MailService;
import com.murano.oms.base.service.TimeService;
import flextrade.flexvision.fx.position.PositionRollOverReminderEmailTask;

import static com.murano.oms.base.feature.SupportedFeature.MANUALLY_ROLL_OVER_REMINDER_EMAIL;

@Configuration
public class SchedulerConfig {

    @Value("${rollover.timezone:UTC}")
    private String rollOverTimeZone;

    @Value("${rollover.cron.expression:0/5 * * ? * SAT-SUN}")
    private String rolloverCronExpression;

    @Value("${rollover.url:}")
    private String rollOverUrl;

    @Value("${rollover.support.email:}")
    private String rollOverSupportEmail;

    @Bean
    public SchedulerFactoryBean createScheduler(FeatureService featureService, ObjectMapper objectMapper, MailService mailService,
                                                RestTemplate restTemplate, TimeService timeService) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("quartz.properties");

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(PropertiesLoaderUtils.loadProperties(classPathResource));
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);
        if (featureService.isActive(MANUALLY_ROLL_OVER_REMINDER_EMAIL)) {
            schedulerFactoryBean.setTriggers(createPositionManualRollerOverReminderEmailCronTrigger(objectMapper, mailService, restTemplate, timeService).getObject());
        }

        return schedulerFactoryBean;
    }

    @Bean
    public JobDetailFactoryBean createPositionRollOverReminderEmailJobDetailFactoryBean(ObjectMapper objectMapper, MailService mailService,
                                                                                        RestTemplate restTemplate, TimeService timeService) {
        Map<String, ? super Object> jobDataMap = createJobDataMap(objectMapper, mailService, restTemplate, timeService);

        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(PositionRollOverReminderEmailTask.class);
        factory.setName("position-roll-over-reminder-task");
        factory.setJobDataAsMap(jobDataMap);

        return factory;
    }

    @Bean
    public CronTriggerFactoryBean createPositionManualRollerOverReminderEmailCronTrigger(ObjectMapper objectMapper, MailService mailService,
                                                                                         RestTemplate restTemplate, TimeService timeService) {
        CronTriggerFactoryBean positionManualRollOverEmailCronTriggerFactoryBean = new CronTriggerFactoryBean();
        positionManualRollOverEmailCronTriggerFactoryBean.setJobDetail(createPositionRollOverReminderEmailJobDetailFactoryBean(objectMapper, mailService, restTemplate, timeService).getObject());
        positionManualRollOverEmailCronTriggerFactoryBean.setStartDelay(3000);
        positionManualRollOverEmailCronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone(rollOverTimeZone));
        positionManualRollOverEmailCronTriggerFactoryBean.setCronExpression(rolloverCronExpression);
        return positionManualRollOverEmailCronTriggerFactoryBean;
    }

    private Map<String, ? super Object> createJobDataMap(ObjectMapper objectMapper, MailService mailService, RestTemplate restTemplate, TimeService timeService) {
        Map<String, ? super Object> jobDataMap = new HashMap<>();
        jobDataMap.put("objectMapper", objectMapper);
        jobDataMap.put("rollOverUrl", rollOverUrl);
        jobDataMap.put("mailService", mailService);
        jobDataMap.put("restTemplate", restTemplate);
        jobDataMap.put("timeService", timeService);
        jobDataMap.put("rollOverSupportEmail", rollOverSupportEmail);
        return jobDataMap;
    }
}
