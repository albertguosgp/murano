package com.murano.oms.steps;

import org.springframework.boot.test.WebIntegrationTest;

import cucumber.api.java.Before;

@WebIntegrationTest
public class ScenarioHook extends AbstractSteps {

    @Before("@cleanup")
    public void cleanup() {
        flyway.clean();
        flyway.migrate();
    }
}
