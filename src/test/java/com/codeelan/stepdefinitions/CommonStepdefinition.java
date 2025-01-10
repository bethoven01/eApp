package com.codeelan.stepdefinitions;

import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.PageObjectManager;
import com.codeelan.libraies.TestContext;
import com.epam.healenium.SelfHealingDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class CommonStepdefinition extends FLUtilities {

    private static final Logger Log = LogManager.getLogger(CommonStepdefinition.class);
    private SelfHealingDriver driver;
    private TestContext testContext;
    private JavascriptExecutor executor = null;

    public CommonStepdefinition(TestContext context) {
        testContext = context;
    }

    @Given("Open page {string}")
    public void userIsOnFLLoginPage(String url) {
        configProperties.setProperty("QA.url", url);
        System.out.println("url == " + url);
        openLoginPage(driver, testContext);
    }

    @Then("Verify Page Title is {string}")
    public void verifyPageTitleIs(String title) {
        System.out.println("Expected Title"+title);
        System.out.println("Title name "+((ChromeDriver) testContext.getDriver().getDelegate()).getTitle());
        System.out.println( "Session ID "+((ChromeDriver) testContext.getDriver().getDelegate()).getSessionId().toString());
        Assert.assertEquals("Page title verification failed!", title, ((ChromeDriver) testContext.getDriver().getDelegate()).getTitle().trim());
    }

    @Then("Enter value {string} in {string} having {string} {string}")
    public void enterValueInTextboxHaving(String valueToSend, String wizardType, String locator, String attributeValue) {
        sendKeys(driver, elementByLocator(driver, locator, null, null, attributeValue), valueToSend);
    }

    @Then("Enter date value {string} in {string} having {string} {string}")
    public void enterDateValueInTextboxHaving(String valueToSend, String wizardType, String locator, String attributeValue) {
        sendKeysJS(driver, elementByLocator(driver, locator, null, null, attributeValue), valueToSend);
    }

    @Then("Enter value from JSON {string} in {string} having {string} {string}")
    public void enterValueInTextboxJSON(String valueToSend, String wizardType, String locator, String attributeValue) {
        sendKeys(driver, elementByLocator(driver, locator, null, null, attributeValue), testContext.getMapTestData().get(valueToSend));
    }

    @Then("Click element {string} having {string} {string}")
    public void clickButtonHaving(String wizardType, String locator, String attributeValue) {
        clickElement(driver, elementByLocator(driver, locator, null, null, attributeValue));
    }

    @Then("Click date element {string} having {string} {string}")
    public void clickDateHaving(String wizardType, String locator, String attributeValue) {
        clickElement(driver, elementByLocator(driver, locator, null, null, attributeValue));
    }

    @Then("Verify Default Value of {string} having {string} {string} is {string}")
    public void verifyDefValue(String wizardType, String locator, String attributeValue, String value) {
        Assert.assertTrue(value.equalsIgnoreCase(verifyDefaultValue(driver, elementByLocator(driver, locator, null, null, attributeValue))));
    }

    @Then("Verify value is {string} for {string} having {string} {string}")
    public void verifyValue(String value, String wizardType, String locator, String attributeValue) {
        Assert.assertTrue(value.equalsIgnoreCase(verifyDefaultValue(driver, elementByLocator(driver, locator, null, null, attributeValue))));
    }

    @Then("Verify Placeholder of {string} having {string} {string} is {string}")
    public void verifyPlaceholder(String wizardType, String locator, String attributeValue, String value) {
        Assert.assertTrue(value.equalsIgnoreCase(verifyPlaceholder(driver, elementByLocator(driver, locator, null, null, attributeValue))));
    }

    @Then("Verify Max Length of {string} having {string} {string} is {string}")
    public void verifyMaxlength(String wizardType, String locator, String attributeValue, String value) {
        Assert.assertTrue(value.equalsIgnoreCase(verifyMaxlength(driver, elementByLocator(driver, locator, null, null, attributeValue))));
    }

    @Then("Select value {string} for dropdown {string} having {string} {string}")
    public void selectValueDropdownHaving(String option, String type, String locator, String attributeValue) {
        new Select(elementByLocator(driver, locator, null, null, attributeValue)).selectByVisibleText(option);
    }

    @Then("Click multiple element {string} having {string} {string}")
    public void selectValueMultipleHaving(String type, String locator, String attributeValue) {
        WebElement element = elementByLocator(driver, locator, null, null, attributeValue);
        driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL);
        element.click();
    }

    @Then("Click link {string} having text as {string}")
    public void clickLink(String locator, String attributeValue) {
        String xPath = "//" + locator.toLowerCase() + "[text()='" + attributeValue + "' or @title='" + attributeValue + "']";
        WebElement element = elementByLocator(driver, "xPath", null, null, xPath);
        element.click();
    }

    @Then("Verify Default Value of dropdown {string} having {string} {string} is {string}")
    public void verifyDefaultValueDropdown(String type, String locator, String attributeValue, String value) {
        new Select(elementByLocator(driver, locator, null, null, attributeValue)).getFirstSelectedOption().getText().equalsIgnoreCase(value);
    }

    @Then("Verify dropdown value is {string} for {string} having {string} {string}")
    public void verifyValueDropdown(String value, String type, String locator, String attributeValue) {
        new Select(elementByLocator(driver, locator, null, null, attributeValue)).getFirstSelectedOption().getText().equalsIgnoreCase(value);
    }

    @Then("Verity Default value of checkbox {string} having {string} {string} is {string}")
    public void verifyDefaultValueCheckbox(String type, String locator, String attributeValue, String value) {
        WebElement element = elementByLocator(driver, locator, null, null, attributeValue);
        clickElement(driver, element);
        Assert.assertTrue(String.valueOf(executor.executeScript("return arguments[0].defaultChecked;", element)).equalsIgnoreCase(value));
        clickElement(driver, element);
    }

    @Then("Verify Default Value of checkbox having {string} {string} is {string}")
    @Then("Verify Default Value of radio having {string} {string} is {string}")
    public void verifyDefaultValueRadio(String locator, String attributeValue, String value) {
        WebElement element = elementByLocator(driver, locator, null, null, attributeValue);
        value = value.equalsIgnoreCase("checked") ? "true" : "false";
//        clickElement(driver, element);
        Assert.assertTrue(String.valueOf(executor.executeScript("return arguments[0].defaultChecked;", element)).equalsIgnoreCase(value));
//        clickElement(driver, element);
    }

    @Then("Verify checkbox value is {string} for {string} having {string} {string}")
    @Then("Verify radio value is {string} for {string} having {string} {string}")
    public void verifyValueRadio(String value, String type, String locator, String attributeValue) {
        WebElement element = elementByLocator(driver, locator, null, null, attributeValue);
        value = value.equalsIgnoreCase("checked") ? "true" : "false";
        Assert.assertTrue(String.valueOf(executor.executeScript("return arguments[0].checked;", element)).equalsIgnoreCase(value));
    }

    @Then ("{string} {string} having {string} {string}")
    public void checkValue (String action, String type, String locator, String attributeValue) {
        WebElement element = elementByLocator(driver, locator, null, null, attributeValue);
//        clickElement(driver, element);
        boolean checked = Boolean.parseBoolean(executor.executeScript("return arguments[0].checked", element).toString());
        if(checked && action.equalsIgnoreCase("Uncheck"))
            clickElement(driver, element);
        if(!checked && action.equalsIgnoreCase("Check"))
            clickElement(driver, element);
    }

    @Given("User is on login page for TestCase {string} on browser {string}")
    public void userIsOnLoginPage(String testCaseID, String browser) {
        if (testContext.getDriver() == null) {
            testContext.setDriver(getWebDriver(testContext, browser));
        }
        testContext.setPageObjectManager(new PageObjectManager(testContext.getDriver()));
        driver = testContext.getDriver();
        executor = (JavascriptExecutor) driver;
        commonSetup(testCaseID);
    }

    private void commonSetup(String testCaseID) {
        testContext.setTestCaseID(testCaseID);
        testContext.setScreenshotFolderName(testCaseID);
        System.out.println("Environment = " + testContext.getEnvironment());
        System.out.println("ApplicationType = " + testContext.getAppType());
        System.out.println("TestCaseID = " + testContext.getTestCaseID());
        System.out.println("CaptureScreenshot = " + testContext.getCaptureScreenshot());
        System.out.println("ScreenshotFolder = " + testContext.getScreenshotFolderName());
        testContext.setMapTestData(getTestData(testCaseID, testContext));
        Log.info("TEST CASE {} STARTED", testCaseID);
    }
}