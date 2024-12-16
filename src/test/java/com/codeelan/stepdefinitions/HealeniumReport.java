package com.codeelan.stepdefinitions;

import com.epam.healenium.SelfHealingDriver;
import com.codeelan.libraies.FLUtilities;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public class HealeniumReport extends FLUtilities {
    private static SelfHealingDriver driver = null;

    public static void main(String[] args) {
        WebDriver delegate = null;
        delegate = new ChromeDriver();

        driver = SelfHealingDriver.create(delegate);
        driver.manage().window().maximize();

        try {
            // Navigate to the page with Selenium
            driver.get("http://localhost:7878/healenium/report/");

            // Find elements with the class names using XPath
            while (driver.findElement(By.xpath("//div[@class='selector-value-row']/div[@class='table-column locator-column']/span")).getText().equalsIgnoreCase("There are no reports and no healings")){
                driver.findElement(By.xpath("(//div[@class='report-link-list-cl active']/../following-sibling::div/div/a)[1]")).click();
                //((JavascriptExecutor) driver).executeScript("arguments[0].click();",  driver.findElement(By.xpath("(//div[@class='report-link-list-cl active']/../following-sibling::div/div/a)[1]")));
            }

            List<WebElement> healedLocator = driver.findElements(By.xpath("//div[@class='selector-value-row']/div[@class='table-column locator-column']/span"));
            // Iterate and take screenshots
            for (int i = 0; i < healedLocator.size(); i++) {

                // Click the element to expand or reveal more content
                WebElement element = healedLocator.get(i);

                int height = element.getSize().getHeight();
                int weight = element.getSize().getWidth();
                System.out.println("Old Height "+height);
                System.out.println("Old weight "+weight);

                System.out.println(element.getLocation().getX());
                System.out.println(element.getLocation().getY());

                System.out.println("New Height "+element.getRect().getHeight());
                System.out.println("New Height "+element.getRect().getWidth());

                element.click();

                //syncElement(driver,driver.findElement(By.xpath("(//div[@class='table-column locator-column healing-details'])[" + String.valueOf(i+1) + "]")), EnumsCommon.TOCLICKABLE.getText());
                String getTextForHeal = driver.findElement(By.xpath("(//div[@class='table-column locator-column healing-details'])[" + String.valueOf(i + 1) + "]")).getText()
                        .replaceAll("Failed", "\nFailed")
                        .replaceAll("Healed", "\nHealed")
                        .replaceAll("Score", "\nScore");

                Thread.sleep(1000); // Consider using WebDriverWait for better synchronization

                // Take a screenshot of the updated state
                System.out.println("The Text Heal -> " + getTextForHeal);
                takeScreenshot(driver, By.xpath("//img[@id='myImg']"), i, getTextForHeal);

                // Optionally, click again to collapse or restore to initial state
//                element.click();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            driver.quit();
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
