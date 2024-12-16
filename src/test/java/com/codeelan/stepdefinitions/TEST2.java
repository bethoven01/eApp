package com.codeelan.stepdefinitions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TEST2 {
    private static String jsonData = "";

    public static void main(String[] args) {
        System.out.println(getSchemaFromResponse("{\"messageid\":12,\"name\":\"TEST case name\",\"email\":\"testcaseemail@gmail.com\",\"phone\":\"777867676768768\",\"subject\":\"TESTCase 1 TESTCase 1\",\"description\":\"TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 \"}"));
    }

    public static String getSchemaFromResponse(String result) {
        RestAssured.baseURI = "https://www.liquid-technologies.com/api/Converter";

        Map<String, String> requestHeaders = new HashMap<String, String>() {{
            put("accept", "*/*");
            put("content-type", "application/json; charset=UTF-8");
            put("origin", "https://www.liquid-technologies.com");
//            put("priority", "u=1, i");
            put("referer", "https://www.liquid-technologies.com/online-json-to-schema-converter");
        }};


        String jsonValue = "{\n" +
                "  \"messageid\": 12,\n" +
                "  \"name\": \"TEST case name\",\n" +
                "  \"email\": \"testcaseemail@gmail.com\",\n" +
                "  \"phone\": \"777867676768768\",\n" +
                "  \"subject\": \"TESTCase 1 TESTCase 1\",\n" +
                "  \"description\": \"TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 TESTCase 1 \"\n" +
                "}";


        String jsonSchemaPojo = "{\n" +
                "  \"Captcha\": \"Ignore\",\n" +
                "  \"Filename\": \"sample.json\",\n" +
                "  \"Type\": \"json\",\n" +
                "  \"Data\": \"{\\n  \\\"Captcha\\\": \\\"Ignore\\\",\\n  \\\"Filename\\\": \\\"sample.json\\\",\\n  \\\"Type\\\": \\\"json\\\",\\n  \\\"Data\\\": \\\"jsonValue\\\",\\n  \\\"TargetType\\\": \\\"json-schema\\\",\\n  \\\"Arguments\\\": {\\n    \\\"arrayRules\\\": \\\"TupleTyping\\\",\\n    \\\"defaultAdditionalItems\\\": null,\\n    \\\"defaultAdditionalProperties\\\": null,\\n    \\\"inferEnums\\\": true,\\n    \\\"makeRequired\\\": true,\\n    \\\"indent\\\": \\\"1\\\",\\n    \\\"indentChar\\\": \\\" \\\",\\n    \\\"quoteChar\\\": \\\"\\\\\\\"\\\",\\n    \\\"quoteNames\\\": true\\n  }\\n}\",\n" +
                "  \"TargetType\": \"json-schema\",\n" +
                "  \"Arguments\": {\n" +
                "    \"arrayRules\": \"TupleTyping\",\n" +
                "    \"defaultAdditionalItems\": null,\n" +
                "    \"defaultAdditionalProperties\": null,\n" +
                "    \"inferEnums\": true,\n" +
                "    \"makeRequired\": true,\n" +
                "    \"indent\": \"2\",\n" +
                "    \"indentChar\": \" \",\n" +
                "    \"quoteChar\": \"\\\"\",\n" +
                "    \"quoteNames\": true\n" +
                "  }\n" +
                "}";
        String jsonSchemaPojo1 = "{\n" +
                "  \"Captcha\": \"Ignore\",\n" +
                "  \"Filename\": \"sample.json\",\n" +
                "  \"Type\": \"json\",\n" +
                "  \"Data\": \"jsonValue\",\n" +
                "  \"TargetType\": \"json-schema\",\n" +
                "  \"Arguments\": {\n" +
                "    \"arrayRules\": \"TupleTyping\",\n" +
                "    \"defaultAdditionalItems\": null,\n" +
                "    \"defaultAdditionalProperties\": null,\n" +
                "    \"inferEnums\": true,\n" +
                "    \"makeRequired\": true,\n" +
                "    \"indent\": \"2\",\n" +
                "    \"indentChar\": \" \",\n" +
                "    \"quoteChar\": \"\\\"\",\n" +
                "    \"quoteNames\": true\n" +
                "  }\n" +
                "}";

        String requestBody = jsonSchemaPojo.replace("jsonValue", jsonValue);

        System.out.println(requestBody);

        Response response = RestAssured
                .given()
                .baseUri("https://www.liquid-technologies.com/api/Converter")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Host", "www.liquid-technologies.com")
                .header("Origin", "https://www.liquid-technologies.com")
                .header("Referer", "https://www.liquid-technologies.com/online-json-to-schema-converter")
                .body(requestBody)
                .post();

        // Validate the response
        if (response.statusCode() == 200) {
            System.out.println("Schema Generated Successfully:");
            System.out.println(response.getBody().prettyPrint());
        } else {
            System.err.println("Request failed with status code: " + response.statusCode());
            System.err.println("Response: " + response.getBody().prettyPrint());
        }
        return response.getBody().toString();
    }
}