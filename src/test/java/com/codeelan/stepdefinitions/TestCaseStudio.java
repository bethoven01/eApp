package com.codeelan.stepdefinitions;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class TestCaseStudio {
    private SelfHealingDriver driver = null;

    public static void main(String[] args) throws InterruptedException {

        String extensionPath = "C:\\Users\\Admin\\Downloads\\LOOPJJEGNLCCNHGFEHEKECPANPMIELCJ_1_7_5_0.crx";
        // Verify if the .crx file exists
        File extensionFile = new File(extensionPath);
        if (!extensionFile.exists()) {
            System.out.println("Test case studio crx file not found at specified path.");
            return;
        }

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new"); // Enable headless mode
        options.addArguments("--window-size=1920,1080");
        options.addExtensions(extensionFile);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        driver.get("chrome-extension://loopjjegnlccnhgfehekecpanpmielcj/testCaseStudio/studioWindow.html");

        String testCaseStudio = driver.getWindowHandle();

        driver.switchTo().newWindow(WindowType.TAB);

        driver.get("https://www.google.com");
        driver.findElement(By.name("q")).sendKeys("Codeelan");
        driver.findElement(By.name("q")).submit();
        driver.close();

        driver.switchTo().window(testCaseStudio);
        System.out.println(driver.getTitle());
        Thread.sleep(3000);

        driver.findElement(By.xpath("//input[@placeholder='Set the TestCase Name here']")).sendKeys("Google test case");
        driver.findElement(By.id("save_btn")).click();
        Thread.sleep(3000);
        driver.quit();
    }
}

