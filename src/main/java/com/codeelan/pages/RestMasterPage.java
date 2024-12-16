package com.codeelan.pages;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epam.healenium.SelfHealingDriver;
import com.codeelan.libraies.FLUtilities;
import com.codeelan.libraies.TestContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.restassured.response.Response;
import com.codeelan.libraies.RestAPICalls;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.support.PageFactory;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestMasterPage extends FLUtilities {

    public Response response;
    public String colVal;
    private final Logger log = LogManager.getLogger(RestMasterPage.class);
    public String st1 = "";
    private RestMasterPage RESTMaster;

    public RestMasterPage(SelfHealingDriver driver) {
        initElements(driver);
    }

    public Response callRESTservice(String restUrl, String method, Map<String, String> map, String counterAPI, TestContext testContext, RestAPICalls rest_All) {
        log.info("Inside callRESTservice()");
        response = rest_All.test(restUrl, method, map, counterAPI, testContext);
        return response;
    }

    private void initElements(SelfHealingDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public List<String> saveResponseFields(Response response, String key, TestContext testContext, RestAPICalls rest_All) {
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        Object object = jsonObject;
        String lastKey = "";
        String originalKey = "";
        String tag;
        int index = 0;

        originalKey = key;
        object = (JSONObject) jsonObject;
        lastKey = key;
        if (key.contains("/")) {
            lastKey = key.substring(key.lastIndexOf("/") + 1);
            key = key.substring(0, key.lastIndexOf("/"));
            for (String jsonKey : key.split("/")) {
                tag = jsonKey;

                if (tag.contains("[")) {
                    index = Integer.parseInt(jsonKey.substring(jsonKey.indexOf("[") + 1, jsonKey.indexOf("]")));
                    tag = jsonKey.substring(0, jsonKey.indexOf("["));
                }
                object = rest_All.getKey((JSONObject) object, tag);
                if (object instanceof JSONArray)
                    object = ((JSONArray) object).get(index);
            }
        }
        if (lastKey.contains("[")) {
            index = Integer.parseInt(lastKey.substring(lastKey.indexOf("[") + 1, lastKey.indexOf("]")));
            lastKey = lastKey.substring(0, lastKey.indexOf("["));
        }
        object = rest_All.getKey((JSONObject) object, lastKey);
        if (object instanceof JSONArray)
            object = ((JSONArray) object).get(index);

        return Arrays.asList(originalKey, object.toString());
    }
}