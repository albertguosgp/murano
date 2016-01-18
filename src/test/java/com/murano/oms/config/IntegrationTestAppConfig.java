package com.murano.oms.config;

import com.icegreen.greenmail.spring.GreenMailBean;
import com.murano.oms.base.feature.impl.CachedFeatureServiceImpl;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

import com.murano.oms.base.feature.FeatureService;

@Configuration
public class IntegrationTestAppConfig {

    @Value("${spring.mail.host: localhost}")
    private String smtpHost;

    @Value("${spring.mail.port: 25}")
    private int smtpPort;

    @Value("${spring.mail.default-encoding: UTF-8}")
    private String defaultEmailEncoding;

    @Bean(name = "flyway")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db/migration");
        flyway.clean();
        flyway.migrate();

        return flyway;
    }

    @Bean
    @DependsOn(value = {"flyway"})
    public FeatureService createFeatureService() {
        return new CachedFeatureServiceImpl();
    }

    @Bean
    public GreenMailBean greenMail() {
        GreenMailBean greenMail = new GreenMailBean();

        return greenMail;
    }
}
