package com.codeelan.libraies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.util.stream.Collectors.*;

import org.bouncycastle.tsp.TSPUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.RequestId;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URLDecoder;
import java.util.*;
import java.io.*;

public class RecordActions {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static WebDriver driver;
    private static TestContext testContext;
    static String encodedJsonData = "";

    static Map<String, Map<String, String>> parentMap = new HashMap<>();
    static List<Map<String, String>> a = new ArrayList<>();
    static int stepsCount = 1;
    static Map<Integer, Map<String, String>> stepsMap = new HashMap<>();
    static Map<Object, Map<String, String>> resultantMap = new HashMap<>();
    static Map<String, Map<String, String>> elementLocatorMap = new HashMap<>();
    static JavascriptExecutor executor = null;
    static List<String> requestDataTypes = Arrays.asList("Stylesheet", "Script", "Image", "Other", "Font");
    static List<String> ignoredRequest = Arrays.asList("ads", "google-analytics", "analytics.google");
//    static String url = "https://www.irctc.co.in/nget/profile/user-signup";

    public static void main(String[] args) throws IOException, ParseException {
        Map<String, String> flowInterfaceMap = new HashMap<>();
        Map<String, Object> preferences = new HashMap<>();
        boolean flag = true;
        JSONObject jsonTestData = new JSONObject();
        JSONObject masterJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        //Flow interface sheet
        String featureName = "Login functionality";
        String description = "Verify login functionality";
        String scenario = args[0];
        String testCaseName = "Verify login functionality";
        String testCaseWorkbook = "Product1.xlsx";
        String testCaseWorkbook1 = "API.xlsx";
        String execute = "Yes";
        String url = args[1];
        System.out.println(scenario);
        System.out.println(url);

        if (!checkScenarioExists(scenario)) {
            System.out.println("Scenario already exists. Please check");
            return;
        }

        deleteRunnerFeature(EnumsCommon.RESPONSE_FILES_PATH.getText() + scenario);

        AtomicInteger counter = new AtomicInteger(0);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(getChromeOptions());
        executor = (JavascriptExecutor) driver;

        DevTools devTools = ((ChromiumDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.requestWillBeSent(), entry -> {
            if (URLDecoder.decode(entry.getRequest().getUrl()).contains(url) && !(URLDecoder.decode(entry.getRequest().getUrl()).contains("TypeError")) && !(requestDataTypes.stream().anyMatch(entry.getType().toString()::contains) || ignoredRequest.stream().anyMatch(URLDecoder.decode(entry.getRequest().getUrl())::contains))) {
                String postDataNL = entry.getRequest().getPostData().toString();
                Map<String, String> tempHeader = new HashMap<>();
                postDataNL = postDataNL.substring(postDataNL.indexOf("Optional") + 9).replaceAll("empty", "");
                Map<String, String> newResult = new HashMap<>();
                Map<String, String> newAssertionResult = new HashMap<>();
                String responseBody = "";
                String responseFileName = "";
                String responseFileNameSchema = "";
                int countRequest = counter.incrementAndGet();

                newResult.put("Status", "200");
                newResult.put("Steps", "Call API");
                newResult.put("BaseURI", URLDecoder.decode(entry.getRequest().getUrl()));
                newResult.put("EndPoint", "");
                newResult.put("Method", entry.getRequest().getMethod());
                newResult.put("Params", "");
                newResult.put("Auth", "");
                for (String key : entry.getRequest().getHeaders().keySet())
                    tempHeader.put(key, entry.getRequest().getHeaders().get(key).toString());

                String s = tempHeader.entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(joining(" | "));

                newResult.put("Headers", s);
                newResult.put("Body", postDataNL);
                newResult.put("Scripts", "");
                newResult.put("Field", "");
                newResult.put("Response", responseFileName);
                newResult.put("Schema", responseFileNameSchema);

                resultantMap.put(countRequest, newResult);

                try {
                    responseBody = new WebDriverWait(driver, Duration.ofSeconds(60))
                            .ignoring(DevToolsException.class)
                            .ignoring(TimeoutException.class)
                            .until(driver -> devTools.send(Network.getResponseBody(entry.getRequestId())).getBody());

                } catch (DevToolsException e) {
                    System.out.println("No response recieved for request ID " + entry.getRequestId());
                } finally {
                    if (!responseBody.equals("")) {
                        try {
                            responseFileName = EnumsCommon.RESPONSE_FILES_PATH.getText() + scenario + "\\API_Response" + countRequest + ".json";
                            File responseFileInput = new File(responseFileName);
                            responseFileInput.getParentFile().mkdirs();
                            if (!responseFileInput.exists())
                                responseFileInput.createNewFile();
                            FileWriter responseFile = new FileWriter(responseFileName);
                            BufferedWriter writer = new BufferedWriter(responseFile);
                            writer.flush();
                            writer.write(responseBody);
                            writer.close();

                            if (isJSONValid(responseBody)) {
                                encodedJsonData = URLEncoder.encode(responseBody, StandardCharsets.UTF_8.toString());
                                String result = "jsonData=" + encodedJsonData + "&lang=schema";
                                String schema = getSchemaFromResponse(result, scenario, countRequest);
                                responseFileNameSchema = EnumsCommon.RESPONSE_FILES_PATH.getText() + scenario + "\\API_ResponseSchema" + countRequest + ".json";
                                File responseFileSchema = new File(responseFileNameSchema);
                                responseFileSchema.getParentFile().mkdirs();
                                if (!responseFileSchema.exists())
                                    responseFileSchema.createNewFile();
                                responseFile = new FileWriter(responseFileSchema);
                                writer = new BufferedWriter(responseFile);
                                writer.flush();
                                writer.write(schema);
                                writer.close();

                            }
                        } catch (IOException e) {
                            System.out.println("File handling issue");
                        }
                    }
                    newResult.put("Response", responseFileName);
                    newResult.put("Schema", responseFileNameSchema);

                    if (resultantMap.containsKey(countRequest)) {
                        Map<String, String> tempMap = resultantMap.get(countRequest);
                        if (tempMap.containsKey("Response"))
                            tempMap.put("Response", responseFileName);
                        resultantMap.put(countRequest, tempMap);
                    }
                }
            }
        });

// Add listener for response received
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Map<String, String> newResult = new HashMap<>();
            RequestId requestId = responseReceived.getRequestId();
            Response response = responseReceived.getResponse();
            int countRequest = counter.get();
            if (resultantMap.containsKey(countRequest)) {
                Map<String, String> tempMap = resultantMap.get(countRequest);
                Map<String, String> tempAssertionMap = new HashMap<>();
                if (tempMap.containsKey("Status")) {
                    tempMap.put("Status", String.valueOf(response.getStatus()));
                }
                resultantMap.put(countRequest, tempMap);
            }
        });


        driver.get(url);

        stepsMap.put(stepsCount++, stepsOpenPage(url));
        stepsMap.put(stepsCount++, stepsVerifyPage(driver.getTitle()));
        BufferedReader bfn = new BufferedReader(new InputStreamReader(System.in));

        while (!Boolean.parseBoolean(bfn.readLine())) {
        }

        String script = "return (function () {" +
                " if (!window.localStorage.getItem('capturedEvents')) {" +
                " window.localStorage.setItem('capturedEvents', JSON.stringify([]));" +
                " }" +
                " function getCapturedEvents() {" +
                " var capturedEvents = JSON.parse(window.localStorage.getItem('capturedEvents'));" +
                " return capturedEvents.map(function (event) {" +
                " return { details: event.details }; " +  // Corrected to return an object
                " });" +
                " }" +
                " return getCapturedEvents();" +
                "})();";

        Object result = executor.executeScript(script);
// Cast the result to List<Map<String, Map<String, String>>>
        List<Map<String, Map<String, String>>> events;
        try {
            events = (List<Map<String, Map<String, String>>>) result;
        } catch (ClassCastException e) {
            throw new RuntimeException("Failed to cast JavaScript result to List<Map<String, Map<String, String>>>", e);
        }
        for (Map<String, Map<String, String>> event : events) {
            Map<String, String> details = event.get("details");
            Map<String, String> secondDetails = new HashMap<>();
            if (events.size() != events.indexOf(event) + 1)
                secondDetails = events.get(events.indexOf(event) + 1).get("details");
            String ariaLabel = details.get("ariaLabel");
            String className = details.get("className").replaceAll(" ", ".");
            String defaultValue = details.get("defaultValue");
            String defaultChecked = details.get("defaultChecked");
            String id = details.get("id");
            String maxlength = details.get("maxlength");
            String placeholder = details.get("placeholder");
            String localName = details.get("localName");
            String target = details.get("target");
            String checked = details.get("checked");
            String action = details.get("action");
            String value = details.get("value");
            String pageTitle = details.get("title");
            String type = details.get("type");
            Object attributes = details.get("attributes");
            Object uniqueLocators = details.get("uniqueLocator");
            List<String> locator = (List<String>) uniqueLocators;
            Map<String, String> attrib = attributes instanceof Map<?, ?> ? (Map<String, String>) attributes : new HashMap<>();

            if (!(secondDetails.isEmpty())) {
                if (target.equalsIgnoreCase("select")) {
                     if(action.equalsIgnoreCase("change"))
                        createSteps(ariaLabel, className, defaultValue, defaultChecked, id, localName, target, action, value, pageTitle, attrib, locator, type, checked, maxlength, placeholder);
                } else {
                    if (attrib.containsKey("autocomplete") && !(action.equals(secondDetails.get("action"))) && !(action.equals("click")))
                        createSteps(ariaLabel, className, defaultValue, defaultChecked, id, localName, target, action, value, pageTitle, attrib, locator, type, checked, maxlength, placeholder);
                    else if (attrib.containsKey("autocomplete") && action.equals("change"))
                        createSteps(ariaLabel, className, defaultValue, defaultChecked, id, localName, target, action, value, pageTitle, attrib, locator, type, checked, maxlength, placeholder);
                    else if (!(uniqueLocators.equals(secondDetails.get("uniqueLocator"))))
                        createSteps(ariaLabel, className, defaultValue, defaultChecked, id, localName, target, action, value, pageTitle, attrib, locator, type, checked, maxlength, placeholder);
                }
            } else
                createSteps(ariaLabel, className, defaultValue, defaultChecked, id, localName, target, action, value, pageTitle, attrib, locator, type, checked, maxlength, placeholder);
        }

        createTestCaseSheet(testCaseWorkbook, scenario);
        createAPITestCaseSheet(testCaseWorkbook1, scenario);
        driver.quit();
    }

    public static void createSteps(String ariaLabel, String className, String defaultValue, String defaultChecked, String id, String localName, String target, String action, String value, String pageTitle, Map<String, String> attributes, List<String> locator, String type, String checked, String maxlength, String placeholder) {
        String key = ariaLabel.replaceAll(" ", "");
        Map<String, String> data = parentMap.get(key);
        String locatorType = locator.get(0);
        String locatorValue = locator.get(1);

        if (!locatorType.equalsIgnoreCase("None")) {
            if (attributes.containsKey("type") && !(type.equals("select-one") | type.equals("radio")))
                type = attributes.get("type");
            switch (action) {
                case "change":
                case "input":
                    switch (type) {
                        case "checkbox":
                        case "radio":
                            stepsMap.put(stepsCount++, stepsDefaultValueCheckbox(key, locatorType, locatorValue, type, defaultChecked));
                            stepsMap.put(stepsCount++, stepsCheckValue(key, locatorType, locatorValue, type, checked));
                            stepsMap.put(stepsCount++, stepsVerifyCheckboxValue(key, locatorType, locatorValue, type, checked));
                            break;
                        case "select-one":
                            stepsMap.put(stepsCount++, stepsDefaultValueDropdown(key, locatorType, locatorValue, type, defaultValue));
                            setPlaceholder(key, locatorType, locatorValue, type, placeholder);
                            stepsMap.put(stepsCount++, stepsSelectValue(key, locatorType, locatorValue, type, value));
                            stepsMap.put(stepsCount++, stepsVerifyDropdownValue(key, locatorType, locatorValue, type, value));
                            break;
                        default:
                            if(attributes.containsKey("autocomplete") && action.equals("change"))
                                stepsMap.put(stepsCount++, stepsVerifyValue(key, locatorType, locatorValue, type, value));
                            else {
                                stepsMap.put(stepsCount++, stepsDefaultValue(key, locatorType, locatorValue, type, defaultValue));
                                setMaxLength(key, locatorType, locatorValue, type, maxlength);
                                setPlaceholder(key, locatorType, locatorValue, type, placeholder);
                                stepsMap.put(stepsCount++, stepsEnterValue(key, locatorType, locatorValue, type, value));
                                if(!action.equals("input"))
                                    stepsMap.put(stepsCount++, stepsVerifyValue(key, locatorType, locatorValue, type, value));
                            }
                    }
                    break;
                case "click":
                    switch (type) {
                        case "text":
                            stepsMap.put(stepsCount++, stepsDefaultValue(key, locatorType, locatorValue, type, defaultValue));
                            setMaxLength(key, locatorType, locatorValue, type, maxlength);
                            setPlaceholder(key, locatorType, locatorValue, type, placeholder);
                            break;
                        case "select-one":
                            stepsMap.put(stepsCount++, stepsDefaultValueDropdown(key, locatorType, locatorValue, type, defaultValue));
                            stepsMap.put(stepsCount++, stepsSelectValue(key, locatorType, locatorValue, type, value));
                            stepsMap.put(stepsCount++, stepsVerifyDropdownValue(key, locatorType, locatorValue, type, value));
                            break;
                        default:
                            if (!type.equals("select-one"))
                                stepsMap.put(stepsCount++, stepsClickElement(key, locatorType, locatorValue, type));
                            //stepsMap.put(stepsCount++, stepsVerifyPage(pageTitle));
                    }
                    break;
            }
        }
    }

    public static Map<String, String> stepsDefaultValue(String key, String locatorType, String locatorValue, String
            type, String defaultValue) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Default Value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", defaultValue);
        return stepsInterfaceMap;
    }

    public static void setMaxLength(String key, String locatorType, String locatorValue, String type, String maxlength) {
        if (!maxlength.equals(""))
            stepsMap.put(stepsCount++, stepsMaxLength(key, locatorType, locatorValue, type, maxlength));
    }

    public static void setPlaceholder(String key, String locatorType, String locatorValue, String type, String placeholder) {
        if (!placeholder.equals(""))
            stepsMap.put(stepsCount++, stepsPlaceholder(key, locatorType, locatorValue, type, placeholder));
    }

    public static Map<String, String> stepsMaxLength(String key, String locatorType, String locatorValue, String
            type, String maxLength) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Max Length");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", maxLength);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsPlaceholder(String key, String locatorType, String locatorValue, String
            type, String placeholder) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Placeholder");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", placeholder);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsDefaultValueCheckbox(String key, String locatorType, String
            locatorValue, String type, String defaultValue) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Default Value of " + type);
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", defaultValue);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsDefaultValueDropdown(String key, String locatorType, String
            locatorValue, String type, String defaultValue) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Default Value of dropdown");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", defaultValue);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsEnterValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Enter value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", value);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsVerifyValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", value);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsVerifyDropdownValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify dropdown value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", value);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsVerifyCheckboxValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify " + type + " value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", value);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsCheckValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        if (value.equalsIgnoreCase("true"))
            stepsInterfaceMap.put("Steps", "\"Check\"");
        else
            stepsInterfaceMap.put("Steps", "\"Uncheck\"");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", "");
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsSelectValue(String key, String locatorType, String locatorValue, String
            type, String value) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Select value");
        stepsInterfaceMap.put("Field Name", key);
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        stepsInterfaceMap.put("Test Data", value);
        return stepsInterfaceMap;
    }


    public static Map<String, String> stepsClickElement(String key, String locatorType, String locatorValue, String type) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Click element");
        stepsInterfaceMap.put("Locator Type", locatorType);
        stepsInterfaceMap.put("Common Tag", locatorValue);
        stepsInterfaceMap.put("Wizard Control Types", type);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsVerifyPage(String pageTitle) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Verify Page Title");
        stepsInterfaceMap.put("Test Data", pageTitle);
        return stepsInterfaceMap;
    }

    public static Map<String, String> stepsOpenPage(String url) {
        Map<String, String> stepsInterfaceMap = new HashMap<>();
        stepsInterfaceMap.put("Steps", "Open page");
        stepsInterfaceMap.put("Test Data", url);
        return stepsInterfaceMap;
    }

    public static void appendRows(Map<String, String> flowInterfaceMap, String module) {
        try {
            File file = new File(EnumsCommon.ABSOLUTE_FILES_PATH.getText() + "FlowInterface.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            Sheet sheet = workbook.getSheet(module);
            Iterator<Row> iterator = sheet.iterator();
            int countRow = sheet.getLastRowNum() + 1;
            Row headerRow = iterator.next().getSheet().getRow(0);
            Row row = sheet.createRow(countRow);
            int countCell = 0;
            boolean flag = true;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                if (sheet.getRow(rowNum).getCell(findColumnIndex(headerRow, "Scenario")).toString().equalsIgnoreCase(flowInterfaceMap.get("Scenario"))) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                for (String tags : flowInterfaceMap.keySet())
                    row.createCell(findColumnIndex(headerRow, tags)).setCellValue(flowInterfaceMap.get(tags));
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSheet(String sheetName, XSSFWorkbook book, File file) throws IOException {
        for (int i = book.getNumberOfSheets() - 1; i >= 0; i--) {
            Sheet tmpSheet = book.getSheetAt(i);
            if (tmpSheet.getSheetName().equals(sheetName)) {
                book.removeSheetAt(i);
            }
        }
    }

    public static boolean checkScenarioExists(String scenario) {
        try {
            File file = new File(EnumsCommon.ABSOLUTE_FILES_PATH.getText() + "FlowInterface.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            List<String> modules = Arrays.asList("UI", "API");

            for (String module : modules) {
                Sheet sheet = workbook.getSheet(module);
                Iterator<Row> iterator = sheet.iterator();
                Row headerRow = iterator.next().getSheet().getRow(0);
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    if (sheet.getRow(rowNum).getCell(findColumnIndex(headerRow, "Scenario")).toString().equalsIgnoreCase(scenario)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void createTestCaseSheet(String testCaseWorkbook, String scenario) {
        try {
            Map<String, String> flowInterfaceMap = new HashMap<>();
            File file = new File(EnumsCommon.ABSOLUTE_CLIENTFILES_PATH.getText() + testCaseWorkbook);
            XSSFWorkbook workbook = null;
            if (!file.exists()) {
                file.createNewFile();
                workbook = new XSSFWorkbook();
            } else {
                workbook = new XSSFWorkbook(new FileInputStream(file));
                removeSheet(scenario, workbook, file);
            }
            Sheet sheet = workbook.createSheet(scenario);
            int countRow = 0;

            Row row = sheet.createRow(countRow);
            Iterator<Row> iterator = sheet.iterator();
            row.createCell(countRow++).setCellValue("Steps");
            row.createCell(countRow++).setCellValue("Field Name");
            row.createCell(countRow++).setCellValue("Locator Type");
            row.createCell(countRow++).setCellValue("Attribute");
            row.createCell(countRow++).setCellValue("Common Tag");
            row.createCell(countRow++).setCellValue("Wizard Control Types");
            row.createCell(countRow++).setCellValue("Test Data");
            row.createCell(countRow++).setCellValue("File Name");
            row.createCell(countRow).setCellValue("Steps Range");

            Row headerRow = iterator.next().getSheet().getRow(0);
            for (Integer count : stepsMap.keySet()) {
                row = sheet.createRow(count);
                for (String tags : stepsMap.get(count).keySet())
                    row.createCell(findColumnIndex(headerRow, tags)).setCellValue(stepsMap.get(count).get(tags));
            }

            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            entryFlowInterface(scenario, testCaseWorkbook, "UI");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createAPITestCaseSheet(String testCaseWorkbook, String scenario) {
        try {
            File file = new File(EnumsCommon.ABSOLUTE_CLIENTFILES_PATH.getText() + testCaseWorkbook);
            XSSFWorkbook workbook = null;
            if (!file.exists()) {
                file.createNewFile();
                workbook = new XSSFWorkbook();
            } else {
                workbook = new XSSFWorkbook(new FileInputStream(file));
                removeSheet(scenario, workbook, file);
            }
            Sheet sheet = workbook.createSheet(scenario);
            int countRow = 0;

            Row row = sheet.createRow(countRow);
            Iterator<Row> iterator = sheet.iterator();
            row.createCell(countRow++).setCellValue("Steps");
            row.createCell(countRow++).setCellValue("BaseURI");
            row.createCell(countRow++).setCellValue("EndPoint");
            row.createCell(countRow++).setCellValue("Method");
            row.createCell(countRow++).setCellValue("Params");
            row.createCell(countRow++).setCellValue("Auth");
            row.createCell(countRow++).setCellValue("Headers");
            row.createCell(countRow++).setCellValue("Body");
            row.createCell(countRow++).setCellValue("Scripts");
            row.createCell(countRow++).setCellValue("Response");
            row.createCell(countRow++).setCellValue("Schema");
            row.createCell(countRow++).setCellValue("Status");
            row.createCell(countRow++).setCellValue("Field");
            row.createCell(countRow).setCellValue("Value");

            Row headerRow = iterator.next().getSheet().getRow(0);
            int counterRow = 1;
            for (Object count : resultantMap.keySet()) {
                row = sheet.createRow(counterRow++);
                for (String tags : resultantMap.get(count).keySet())
                    row.createCell(findColumnIndex(headerRow, tags)).setCellValue(resultantMap.get(count).get(tags));
                row = sheet.createRow(counterRow++);
                row.createCell(findColumnIndex(headerRow, "Steps")).setCellValue("Verify status of preceding request");
                row.createCell(findColumnIndex(headerRow, "Value")).setCellValue(resultantMap.get(count).get("Status"));
                if (!resultantMap.get(count).get("Schema").isEmpty()) {
                    row = sheet.createRow(counterRow++);
                    row.createCell(findColumnIndex(headerRow, "Steps")).setCellValue("Verify schema of preceding response");
                    String schema = resultantMap.get(count).get("Schema");
                    schema = schema.substring(schema.lastIndexOf("\\")+1);
                    row.createCell(findColumnIndex(headerRow, "Value")).setCellValue(EnumsCommon.EXPECTEDRESPONSE_FILES_PATH.getText() + scenario + "\\" + schema);
                }
            }

            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            entryFlowInterface(scenario, testCaseWorkbook, "API");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void entryFlowInterface(String scenario, String testCaseWorkbook, String module) {
        Map<String, String> flowInterfaceMap = new HashMap<>();
        flowInterfaceMap.put("Scenario", scenario);
        flowInterfaceMap.put("TestCaseSheet", testCaseWorkbook);
        flowInterfaceMap.put("Execute", "Yes");
        appendRows(flowInterfaceMap, module);
    }

    public static boolean isJSONValid(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String getSchemaFromResponse(String result, String scenario, int countRequest) {
        RestAssured.baseURI = "https://codebeautify.com/LangConv";
        Map<String, String> requestHeaders = new HashMap<String, String>() {{
            put("accept", "text/plain, */*;q=0.01");
            put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            put("origin", "https://jsonformatter.org");
            put("priority", "u=1, i");
            put("referer", "https://sonformatter.org/");
        }};

        RequestSpecification request = RestAssured.given();
        request.headers(requestHeaders);
        request.body(result);
        io.restassured.response.Response response = request.post();
        return response.getBody().asString();
    }

    private static ChromeOptions getChromeOptions() {
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("autofill.profile_enabled", false);
        preferences.put("download.prompt_for_download", false);
        preferences.put("download.extensions_to_open", "applications/pdf");
        preferences.put("plugins.plugins_disabled", "Chrome PDF Viewer");
        preferences.put("plugins.always_open_pdf_externally", true);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", preferences);
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--remote-allow-origins=*");
        File folder = new File(EnumsCommon.EXTENSION_FILES_PATH.getText());
        chromeOptions.addArguments("load-extension=" + folder);
        return chromeOptions;
    }

    public static void waitForPageToLoad(WebDriver driver) {
        String loaderClassName = "ITSpinner";
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60)) // Maximum wait time
                .pollingEvery(Duration.ofMillis(100)) // Polling interval
                .ignoring(Exception.class);

        wait.until(webdriver -> {
            Long loaderCount = (Long) ((JavascriptExecutor) driver).executeScript(
                    "return document.getElementsByClassName('" + loaderClassName + "').length;");
            return loaderCount == 0;
        });
    }

    protected static void deleteRunnerFeature(String folderPath) {
        File folder = new File(folderPath);

        try {
            Path directory = Paths.get(String.valueOf(folder));
            // Recursively delete the directory and its contents
            Files.walk(directory)
                    .sorted(Collections.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            System.out.println("Runner & Feature Folder deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to delete the folder: " + e.getMessage());
        }
    }

    public static int findColumnIndex(Row headerRow, String columnName) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();

        // Iterate through each cell in the header row
        while (cellIterator.hasNext()) {
            // Get the current cell
            Cell cell = cellIterator.next();

            // Check if the current cell's value matches the column name (case-insensitive)
            if (columnName.equalsIgnoreCase(getCellColumnValue(cell))) {
                // Return the index of the cell if the column name matches
                return cell.getColumnIndex();
            }
        }
        return -1; // Column not found
    }

    public static String getCellColumnValue(Cell cell) {
        // If the cell is null, return an empty string; otherwise, return the trimmed string value of the cell
        return cell == null ? "" : cell.toString().trim();
    }

}