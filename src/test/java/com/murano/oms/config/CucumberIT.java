package com.murano.oms.config;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        format = {"pretty", "html:target/cucumber"},
        features = "src/test/resources",
        glue = "flextrade.flexvision.fx.steps")
public class CucumberIT {
}
