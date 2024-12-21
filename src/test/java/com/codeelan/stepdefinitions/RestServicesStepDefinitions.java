package com.codeelan.stepdefinitions;

import java.util.List;
import java.util.Map;

import com.codeelan.libraies.AssertionHelper;
import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.RestAPICalls;
import com.codeelan.libraies.TestContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.qameta.allure.Allure;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.cucumber.java.en.Given;
import com.codeelan.pages.RestMasterPage;
import org.json.JSONObject;
import org.junit.Assert;

public class RestServicesStepDefinitions extends FLUtilities {
//	public RestMasterPage onRestMasterPage;
//	Response response;
//	public RestAPICalls rest_All;
//	String respBody;
//	int respStatus;
//	String str_query = "";
//	private final TestContext testContext;
//	Map<String,String> map;

    private final RestMasterPage onRestMasterPage;
    private final RestAPICalls rest_All;
    private final TestContext testContext;
    private Response response;
    private String respBody;
    private Map<String, String> parametersMap;

    public RestServicesStepDefinitions(TestContext context) {
        this.testContext = context;
        this.rest_All = new RestAPICalls();
        this.onRestMasterPage = context.getPageObjectManager().getRestMasterPage();
    }

    @Given("Call API {int} {string} request {string} URL")
    public void callAPI(int counterAPI, String restURL, String method) {
        response = onRestMasterPage.callRESTservice(restURL, method, String.valueOf(counterAPI), testContext, rest_All);
        respBody = response.getBody().prettyPrint();
    }

    //status code , schema, status line, body ->

    @Given("Save Field from Response {string}")
    public void saveFieldFromResponse(String fields) {
        for (String field : fields.split(", ")) {
            String originalKey = field.substring(0, field.indexOf(":")).trim();
            field = field.substring(field.indexOf(":") + 1).trim();
            List<String> respFields = onRestMasterPage.saveResponseFields(response, field, testContext, rest_All);
            addPropertyValueInJSON(testContext.getTestCaseID(), testContext, originalKey, respFields.get(1));
        }
    }


    @Given("Verify field from Response {string}")
    public void verifyFieldFromResponse(String fields) {
        for (String field : fields.split(", ")) {
            List<String> respFields = onRestMasterPage.saveResponseFields(response, field, testContext, rest_All);
            Assert.assertEquals(respFields.get(1), testContext.getMapTestData().get(respFields.get(0)));
        }
    }

    @Then("Verify status of preceding request {int} is {int}")
    public void verifyStatusCode(int apiCount, int expectedStatusCode) {
        AssertionHelper.assertInt(response.statusCode(), expectedStatusCode, "Verify status code");
    }

    @Then("Verify status line of preceding request {int} is {string}")
    public void verifyResponseTime(int apiCount, Long expectedStatusLine) {
        AssertionHelper.assertLong(response.time(), expectedStatusLine, "Verify response time");
    }


    @Then("Verify schema of preceding response {int} is {string}")
    public void verifyJSONSchema(int apiCount, String jsonSchemaPath) {
        response.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonSchemaPath));
    }

//    Then Verify response body Call API 8 for key "payload" should be empty
//    Then Verify response body Call API 13 for key "merchant_name" should be equal to "rajesh"
//    Then Verify response body Call API 13 for key "merchant_name" should not be null
//    Then Verify response body Call API 13 for node key value "payload.merchants[0].merchant_name" should not be null
//
    @Then("Verify response body Call API {int} for key {string} should be equal to {string}")
    public void verifyResponseBodyCallAPIForKeyShouldBeEqualTo(int apiCount, String actualValue, String expectedValue) {
//        String actualValue1 = JsonPath.read(respBody, actualValue);
//        AssertionHelper.assertFieldValue(actualValue1,expectedValue,actualValue);

        JSONObject JsonObject = new JSONObject(response.getBody().asString());
        AssertionHelper.assertFieldValue(RestAPICalls.getKey(JsonObject, actualValue).toString(), expectedValue, actualValue);
    }

    @Then("Verify response body Call API {int} for key {string} should not be null")
    public void verifyResponseBodyCallAPIForKeyShouldNotBeNull(int apiCount, String actualValue) {

        JSONObject JsonObject = new JSONObject(response.getBody().asString());
        AssertionHelper.assertNotNull(RestAPICalls.getKey(JsonObject, actualValue).toString(), actualValue);
    }

    @Then("Verify response body Call API {int} for node key value {string} should not be null")
    public void verifyResponseBodyCallAPIForNodeKeyValueShouldNotBeNull(int apiCount, String actualValue) {
        AssertionHelper.assertNotNull(AssertionHelper.getNodeValue(response, actualValue), actualValue);
    }

    @Then("Verify response body Call API {int} for key {string} should be empty")
    public void verifyResponseBodyCallAPIForKeyShouldBeEmpty(int apiCount, String actualValue) {
        AssertionHelper.assertEmpty(AssertionHelper.getNodeValue(response, actualValue), actualValue);
    }
}