package com.codeelan.stepdefinitions;

import com.codeelan.libraies.EnumsCommon;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class test123 {

    public static void main(String[] args) {
        String inputFilePath = EnumsCommon.RESPONSE_FILES_PATH.getText() + "TestScenario6\\API_Response8.json";
        String outputSchemaFilePath = EnumsCommon.RESPONSE_FILES_PATH.getText() + "TestScenario6\\schema.json";

        try {
            // Read JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(new File(inputFilePath));

            // Generate schema
            JsonNode schema = generateSchema(jsonResponse, objectMapper);

            // Write schema to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputSchemaFilePath), schema);

            System.out.println("JSON Schema generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonNode generateSchema(JsonNode jsonNode, ObjectMapper objectMapper) {
        // Base schema node
        ObjectNode schemaNode = objectMapper.createObjectNode();
        schemaNode.put("type", getType(jsonNode));

        if (jsonNode.isObject()) {
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            schemaNode.set("properties", propertiesNode);

            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                propertiesNode.set(fieldName, generateSchema(jsonNode.get(fieldName), objectMapper));
            }
        } else if (jsonNode.isArray()) {
            if (jsonNode.size() > 0) {
                schemaNode.set("items", generateSchema(jsonNode.get(0), objectMapper));
            }
        }

        return schemaNode;
    }

    private static String getType(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            return "object";
        } else if (jsonNode.isArray()) {
            return "array";
        } else if (jsonNode.isTextual()) {
            return "string";
        } else if (jsonNode.isNumber()) {
            return "number";
        } else if (jsonNode.isBoolean()) {
            return "boolean";
        } else if (jsonNode.isNull()) {
            return "null";
        } else {
            return "unknown";
        }
    }
}

