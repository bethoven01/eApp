package com.codeelan.stepdefinitions;

import java.util.List;
import java.util.Map;

import com.codeelan.libraies.AssertionHelper;
import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.RestAPICalls;
import com.codeelan.libraies.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.qameta.allure.Allure;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.cucumber.java.en.Given;
import com.codeelan.pages.RestMasterPage;
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
        Allure.addAttachment("Response Body", respBody);
    }

    //status code , schema, status line, body ->

    @Given("Save Field from Response {string}")
    public void saveFieldFromResponse(String fields) {
        for (String field : fields.split(", ")) {
            String originalKey = field.substring(0, field.indexOf(":")).trim();
            field = field.substring(field.indexOf(":")+1).trim();
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

//
//
//	@SuppressWarnings("deprecation")
//	@Then("^Verify STATUS CODE matches \"(.*)\"$")
//	public void verifySTATUS_CODE(String statCode) throws Throwable {
//
//		int code = Integer.parseInt(statCode);
//		assertEquals(code,respStatus);
//
//	}
//
//	@Then("^Verify GUID matches with the GUID returned from \"(.*)\"$")
//	public void verifyPOST_DATA(String dbQuery, String dbRecord) throws Throwable {
//
//		assertEquals(respBody.toLowerCase().replace("\"",""),DEMORestMaster.getResponse(dbQuery, dbRecord).toLowerCase());
//
//	}


}