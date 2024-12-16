package com.codeelan.runner.TestCases;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "rerun:target/failedrun.txt",
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        features = {"classpath:"},
		tags = "@UI-scenario2",
        glue = {"com.codeelan.stepdefinitions"},
        monochrome = true,
        publish = true
)
public class UI_scenario2_RunTest {
    private static final Logger Log = LogManager.getLogger(UI_scenario2_RunTest.class);
    public static void main(String[] args) {
        // Add the UniqueTestCounter listener to your test run
        JUnitCore core = new JUnitCore();
        core.addListener(new UniqueTestCounter());

        // Run your tests
        core.run(UI_scenario2_RunTest.class);
    }
}