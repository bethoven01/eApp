package com.codeelan.libraies;

import com.epam.healenium.SelfHealingDriver;
import io.cucumber.java.Scenario;
import lombok.Data;

import java.util.HashMap;

@Data
public class TestContext {
    private SelfHealingDriver driver = null;
    private PageObjectManager pageObjectManager = null;
    private Scenario scenario = null;
    private String testCaseID = null;
    private String moduleName = null;
    private String screenshotFolderName = null;
    private String captureScreenshot = null;
    private String appType = null;
    private HashMap<String, String> mapTestData = null;
    private String environment = null;
    private String currentTestUserName = null;
    private String browser = null;
    private String VM_Name = null;
}