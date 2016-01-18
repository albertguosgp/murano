package com.murano.oms.steps;

import com.murano.oms.MuranoOmsMain;

import org.flywaydb.core.Flyway;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.murano.oms.config.IntegrationTestAppConfig;
import lombok.Getter;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest(value = "server.post:0")
@ContextConfiguration(classes = {MuranoOmsMain.class, IntegrationTestAppConfig.class},
        loader = SpringApplicationContextLoader.class)
public abstract class AbstractSteps {
    @Getter
    @Value("${local.server.port}")
    private int port;

    @Autowired
    protected Flyway flyway;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

}
