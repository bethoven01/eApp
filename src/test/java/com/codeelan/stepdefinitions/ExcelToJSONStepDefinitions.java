package com.codeelan.stepdefinitions;

import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.PageObjectManager;
import com.codeelan.libraies.TestContext;
import com.codeelan.pages.E2EFlowDataPage;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebDriver;



public class ExcelToJSONStepDefinitions extends FLUtilities {

    private final E2EFlowDataPage onE2EFlowDataPage;
    private final TestContext testContext;
    private final WebDriver driver;

    public ExcelToJSONStepDefinitions(TestContext context) {
        testContext = context;
        if (testContext.getDriver() == null) {
            testContext.setDriver(getWebDriver(testContext, "Chrome"));
        }
        testContext.setPageObjectManager(new PageObjectManager(testContext.getDriver()));
        driver = testContext.getDriver();
        onE2EFlowDataPage = context.getPageObjectManager().getE2EFlowDataPage();
    }

    /**
     * Create input JSON, runner and feature files from given spec and created flow interface
     *
     * @param excelFile - Spec file provided
     */
    @Given("Create test cases for eApp flow with interface file {string}")
    public void createForesightTestDataInterface(String excelFile) {
        onE2EFlowDataPage.createForesightTestDataInterface(excelFile);
    }
}