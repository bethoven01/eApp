package com.codeelan.stepdefinitions;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TEST1 {
    public static void main(String[] args) {
        // Your JSON data to replace in the "Data" field
        String data = "{\"messageid\":12,\"name\":\"TEST case name\",\"email\":\"testcaseemail@gmail.com\",\"phone\":\"777867676768768\",\"subject\":\"TESTCase 1 TESTCase 1\",\"description\":\"TESTCase 1\"}";

        // Base request body template
        String requestBodyTemplate = " {\n" +
                "                \"Captcha\": \"Ignore\",\n" +
                "                \"Filename\": \"sample.json\",\n" +
                "                \"Type\": \"json\",\n" +
                "                \"Data\": %s,\n" +
                "                \"TargetType\": \"json-schema\",\n" +
                "                \"Arguments\": {\n" +
                "                    \"arrayRules\": \"TupleTyping\",\n" +
                "                    \"defaultAdditionalItems\": null,\n" +
                "                    \"defaultAdditionalProperties\": null,\n" +
                "                    \"inferEnums\": true,\n" +
                "                    \"makeRequired\": true,\n" +
                "                    \"indent\": \"2\",\n" +
                "                    \"indentChar\": \" \",\n" +
                "                    \"quoteChar\": \"\\\\\"\",\n" +
                "                    \"quoteNames\": true\n" +
                "                }\n" +
                "            }";

        // Replace the placeholder with your JSON data
        String requestBody = String.format(requestBodyTemplate, data);

        // Send the POST request
        Response response = RestAssured
                .given()
                .baseUri("https://www.liquid-technologies.com/api/Converter")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Host", "www.liquid-technologies.com")
                .header("Origin", "https://www.liquid-technologies.com")
                .header("Referer", "https://www.liquid-technologies.com/online-json-to-schema-converter")
                .header("Cookie", "ASP.NET_SessionId=ssigbc3qiadxffmse3am0a3w") // Replace with a valid session cookie if needed
                .body(requestBody)
                .post();

        // Validate and print the response
        if (response.statusCode() == 200) {
            System.out.println("Schema Generated Successfully:");
            System.out.println(response.getBody().prettyPrint());
        } else {
            System.err.println("Request failed with status code: " + response.statusCode());
            System.err.println("Response: " + response.getBody().prettyPrint());
        }
    }
}
