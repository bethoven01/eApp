package com.codeelan.stepdefinitions;

import com.epam.healenium.SelfHealingDriver;
import com.codeelan.libraies.PageObjectManager;
import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.TestContext;
import io.cucumber.java.*;
import io.qameta.allure.Allure;

import io.restassured.RestAssured;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Hooks extends FLUtilities {
    private TestContext testContext;
    private SelfHealingDriver driver = null;

    public Hooks(TestContext context) {
        testContext = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        loadConfigData(testContext);
        testContext.setScenario(scenario);
    }
//
//    @BeforeStep
//    public void beforeStep(Scenario scenario) {
//        System.out.println("Before Step Name: " + scenario.getName());
//        System.out.println("Before Step Status: " + scenario.getStatus());
//        // You can send an "In Progress" status here
//    }
//
//    @AfterStep
//    public void afterEachStep(Scenario scenario) {
//
//        StringBuilder scenarioDetails = new StringBuilder();
//        scenarioDetails.append("Scenario Details:\n")
//                .append("Name -> ").append(scenario.getName()).append("\n")
//                .append("ID -> ").append(scenario.getId()).append("\n")
//                .append("Status -> ").append(scenario.getStatus()).append("\n")
//                .append("Line -> ").append(scenario.getLine()).append("\n")
//                .append("Source Tag Names -> ").append(scenario.getSourceTagNames()).append("\n")
//                .append("URI -> ").append(scenario.getUri()).append("\n")
//                .append("Class -> ").append(scenario.getClass().getName());
//
//        // Print all the details at once
//        System.out.println(scenarioDetails.toString());
//
//
//
//
//        String name = scenario.getName().toString();
//        System.out.println("name -> " + name);
//
//        String id = scenario.getId().toString();
//        System.out.println("ID -> " + id);
//
//        String status = scenario.getStatus().toString();
//        System.out.println("status -> " + status);
//
//        String line = scenario.getLine().toString();
//        System.out.println("line -> " + line);
//
//        String tagName = scenario.getSourceTagNames().toString();
//        System.out.println("sourceTagName -> " + tagName);
//
//        String uri = scenario.getUri().toString();
//        System.out.println("uri -> " + uri);
//
//
//        String getClass = scenario.getClass().toString();
//        System.out.println("class -> " + getClass);
//
//        String browserName = testContext.getBrowser();
//
//        Allure.addAttachment("Step finished with status", status);
//        System.out.println("Step finished with status : " + browserName + "|" + name + "-" + status);
//    }

    @After
    public void cleanUp() {
        RestAssured.reset();

        if (!testContext.getScenario().getSourceTagNames().stream().anyMatch(tag -> tag.contains("API") || tag.equals("@Test"))) {

            String sessionID = null;
            WebDriver delegate = testContext.getDriver().getDelegate();

            if (delegate instanceof RemoteWebDriver) {
                sessionID = ((RemoteWebDriver) delegate).getSessionId().toString();
            } else {
                throw new IllegalStateException("Unsupported browser driver: " + delegate.getClass().getName());
            }

            //String sessionID = ((ChromeDriver) testContext.getDriver()).getSessionId().toString();
            System.out.println("Session ID end " + sessionID);

            closeBrowser(testContext);

            // Initialize WebDriver

            if (testContext.getScenario().getSourceTagNames().stream().anyMatch(tag -> tag.contains("UI"))) {

//            String extensionPath = "C:\\Users\\Admin\\Downloads\\undefined 3.17.2.0.crx";
//            // Verify if the .crx file exists
//            File extensionFile = new File(extensionPath);
//            if (!extensionFile.exists()) {
//                System.out.println("Selenium IDE .crx file not found at specified path.");
//            }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless"); // Enable headless mode
//            options.addArguments("--window-size=1920,1080");
//            options.addExtensions(extensionFile);

                WebDriver driver = new ChromeDriver(options);
                driver.manage().window().maximize();

                try {

                    // Navigate to the page with Selenium
                    driver.get("http://localhost:7878/healenium/report/" + sessionID);

                    List<WebElement> healedLocator = driver.findElements(By.xpath("//div[@class='selector-value-row']/div[@class='table-column locator-column']/span"));
                    // Iterate and take screenshots
                    for (int i = 0; i < healedLocator.size(); i++) {
                        try {
                            // Click the element to expand or reveal more content
                            WebElement element = healedLocator.get(i);
                            element.click();

                            //syncElement(driver,driver.findElement(By.xpath("(//div[@class='table-column locator-column healing-details'])[" + String.valueOf(i+1) + "]")), EnumsCommon.TOCLICKABLE.getText());
                            String getTextForHeal = driver.findElement(By.xpath("(//div[@class='table-column locator-column healing-details'])[" + String.valueOf(i + 1) + "]")).getText()
                                    .replaceAll("Failed", "\nFailed")
                                    .replaceAll("Healed", "\nHealed")
                                    .replaceAll("Score", "\nScore");

                            Thread.sleep(1000); // Consider replacing with WebDriverWait
                            System.out.println("The Text Heal -> " + getTextForHeal);

                            // Take a screenshot of the updated state
                            takeScreenshot(driver, By.xpath("//img[@id='myImg']"), i, getTextForHeal);
                        } catch (NoSuchElementException e) {
                            System.err.println("No heal Element present for index: " + i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    driver.quit();
                }
            }
        }
    }

    private static void takeScreenshot(WebDriver driver, By locator, int index, String getTextForHeal) {
        try {
            // Find the element by its locator and index
            List<WebElement> elements = driver.findElements(locator);
            if (index >= elements.size()) {
                System.out.println("Index out of bounds for elements list.");
                return;
            }

            WebElement element = elements.get(index);

            // Scroll to the element to ensure it is in view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            element.click();
            Thread.sleep(1000);

            WebElement ele = driver.findElement(By.xpath("(//img[@id='img01'])[1]"));
            // Check if the element is visible and has non-zero size
            if (ele.isDisplayed() && ele.getSize().width > 0 && ele.getSize().height > 0) {
                // Take a screenshot of the element
                byte[] source = ele.getScreenshotAs(OutputType.BYTES);
                InputStream screenshotInputStream = new ByteArrayInputStream(source);

                // Attach the screenshot to Allure report
                Allure.addAttachment(getTextForHeal, screenshotInputStream);
                driver.findElement(By.xpath("(//img[@id='img01'])[1]/preceding-sibling::span")).click();
//                Allure.addAttachment(getTextForHeal, "text/html",screenshotInputStream,".png");
            } else {
                System.out.println("Element is not visible or has zero width/height.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}