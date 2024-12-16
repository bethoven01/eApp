package com.codeelan.stepdefinitions;

import com.codeelan.libraies.TestContext;
import com.codeelan.pages.E2EFlowDataPage;
import io.cucumber.java.en.Given;


public class ExcelToJSONStepDefinitions {

    private final E2EFlowDataPage onE2EFlowDataPage;
    private final TestContext testContext;

    public ExcelToJSONStepDefinitions(TestContext context) {
        onE2EFlowDataPage = context.getPageObjectManager().getE2EFlowDataPage();
        this.testContext = context;
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