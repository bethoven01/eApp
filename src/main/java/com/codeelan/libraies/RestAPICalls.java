package com.codeelan.libraies;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jayway.jsonpath.JsonPath;
import io.qameta.allure.Allure;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RestAssuredConfig.config;


public class RestAPICalls {
    private Response response;

    public Response call(RequestSpecification request, String method) {

        // Create streams to capture logs
        ByteArrayOutputStream requestLogStream = new ByteArrayOutputStream();
        ByteArrayOutputStream responseLogStream = new ByteArrayOutputStream();

        // Add logging filters to capture request and response
        request.filter(new RequestLoggingFilter(new PrintStream(requestLogStream)));
        request.filter(new ResponseLoggingFilter(new PrintStream(responseLogStream)));

        // Switch-case for handling HTTP methods
        switch (method.toLowerCase()) {

            case "post":
                response = request.log().all().when().post(); // Log request
                break;
            case "get":
                response = request.log().all().when().get(); // Log request
                break;
            case "put":
                response = request.log().all().when().put(); // Log request
                break;
            case "delete":
                response = request.log().all().when().delete(); // Log request
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        // Log the response
        response.then().log().all();

        // Attach captured request log to Allure
        Allure.addAttachment("HTTP Request", "text/plain", requestLogStream.toString());

        // Attach captured response log to Allure
        Allure.addAttachment("HTTP Response", "text/plain", responseLogStream.toString());

        // Close the streams to avoid memory leaks
        try {
            requestLogStream.close();
            responseLogStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response test(String restUrl, String method, String counterAPI, TestContext testContext) {
        String testJSON = testContext.getMapTestData().get(counterAPI).trim();
        restUrl = replacePlaceholders("headers", restUrl, testContext);
        String field = testJSON.contains("field") ? JsonPath.read(testJSON, "$.field").toString().trim() : "";
        String endpoint = testJSON.contains("endpoint") ? JsonPath.read(testJSON, "$.endpoint").toString().trim() : "";
        String param = testJSON.contains("params") ? replacePlaceholders("params", JsonPath.read(testJSON, "$.params").toString().trim(), testContext) : restUrl.contains("?") ? restUrl.substring(restUrl.indexOf("?")+1):"";
        String headers = testJSON.contains("headers") ? replacePlaceholders("headers", JsonPath.read(testJSON, "$.headers").toString().trim(), testContext) : "";
        List<String> fieldList = headers.equals("") ? new ArrayList<>() : Arrays.asList(headers.replaceAll("[{}]", "").split("\\|"));
        List<String> fieldList1 = new ArrayList<>();

        if (!field.equals("")) {
            for (String fieldName : fieldList)
                fieldList1.add(fieldName.replaceAll(field, testContext.getMapTestData().get(field)));
            fieldList = fieldList1;
        }
        Map<String, String> requestHeaders = fieldList.isEmpty() ? new HashMap<>() : fieldList.stream()
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0].trim(), s -> s[1].trim()));
//
//        RestAssured.baseURI = restUrl + param;

        Map<String, String> queryParams = parseQueryParams(param);

        RequestSpecification request = given()
                .config(config().encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("multipart/form-data", io.restassured.http.ContentType.TEXT)))
                .baseUri(restUrl.split("\\?")[0])
                .headers(requestHeaders).queryParams(queryParams);


        String body = "";
        if (testJSON.contains("body")) {
            body = replacePlaceholders("body", JsonPath.read(testJSON, "$.body").toString().trim(), testContext);
            // Add the Json to the body of the request
//            if (testJSON.contains("bodyFields")) {
//                for (String params : JsonPath.read(testJSON, "$.bodyFields").toString().trim().split(" "))
//                    body = body.replaceAll(params, "\"" + JsonPath.read(testJSON, params).toString().trim() + "\"");
//            }
        }
        if(headers.contains("Content-Type=multipart/form-data")) {
            Map<String, String> multipart = parseFormData(body);
            for(String key : multipart.keySet())
                request.multiPart(key, multipart.get(key));
        }
        else
            request.body(body);

        // Post the request and log request/response
        return (call(request, method));
    }

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }


    public static String replacePlaceholders(String parameter, String template, TestContext testContext) {
        // Regular expression to match placeholders of the form ${key}
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        Matcher matcher = pattern.matcher(template);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1); // Extract the key (e.g., 'a', 'b', 'c')
            String replacement = testContext.getMapTestData().get(key).trim(); // Default to "null" if key not found
            if(parameter.equalsIgnoreCase("headers"))
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            else
                matcher.appendReplacement(result, Matcher.quoteReplacement("\"" + replacement + "\""));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static Object getKey(JSONObject json, String key) {
        boolean exists = json.has(key);
        Iterator<?> keys;
        String nextKeys;
        Object value = "";

        if (!exists) {
            keys = json.keys();
            while (keys.hasNext()) {
                nextKeys = (String) keys.next();
                try {
                    if (json.get(nextKeys) instanceof JSONObject) {
                        Object getValue = getKey(json.getJSONObject(nextKeys), key);
                        if (!getValue.toString().isEmpty()) {
                            value = getValue;
                            break;
                        }
                    } else if (json.get(nextKeys) instanceof JSONArray) {
                        JSONArray jsonarray = json.getJSONArray(nextKeys);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            String jsonArrayString = jsonarray.get(i).toString();
                            JSONObject innerJSOn = new JSONObject(jsonArrayString);
                            Object getValue = getKey(innerJSOn, key);
                            if (!getValue.toString().isEmpty()) {
                                value = getValue;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Key doesn't exist in response - " + key);
                }
            }
        } else {
            value = json.get(key);
        }
        return value;
    }

    public static Map<String, String> parseFormData(String formData) {
        Map<String, String> result = new HashMap<>();

        // Regular expression to match form-data fields
        Pattern pattern = Pattern.compile("name=\"(.*?)\"\\s+\\n(.*?)\\n", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(formData);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            result.put(name, value);
        }

        return result;
    }
}