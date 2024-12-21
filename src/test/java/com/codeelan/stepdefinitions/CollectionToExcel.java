package com.codeelan.stepdefinitions;

import com.codeelan.libraies.EnumsCommon;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionToExcel {
    public static void main(String[] args) {
        String inputFilePath = EnumsCommon.ABSOLUTE_FILES_PATH.getText() + "EcomApis.postman_collection.json"; // Replace with your Postman collection file


        try {
            // Parse JSON filewh
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(new File(inputFilePath));

            JsonNode info = root.get("info");
            String outputFilePath = EnumsCommon.ABSOLUTE_FILES_PATH.getText() + info.get("name").asText() + ".xlsx";

            // Create Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(info.get("name").asText());

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Steps");
            headerRow.createCell(1).setCellValue("BaseURI");
            headerRow.createCell(2).setCellValue("EndPoint");
            headerRow.createCell(3).setCellValue("Method");
            headerRow.createCell(4).setCellValue("Params");
            headerRow.createCell(5).setCellValue("Auth");
            headerRow.createCell(6).setCellValue("Headers");
            headerRow.createCell(7).setCellValue("Body");
            headerRow.createCell(8).setCellValue("Scripts");
            headerRow.createCell(9).setCellValue("Response");
            headerRow.createCell(10).setCellValue("Schema");
            headerRow.createCell(11).setCellValue("Status");
            headerRow.createCell(12).setCellValue("Field");
            headerRow.createCell(13).setCellValue("Value");


            // Extract and write data
            JsonNode requests = root.get("item"); // Adjust based on JSON structure
            if (requests != null && requests.isArray()) {
                int rowIndex = 1;
                for (JsonNode item : requests) {

                    JsonNode request = item.get("request");
                    if (request != null) {
                        String auth ="";
                        String baseUrl = extractBaseUrl(request);
                        String endpoint = extractEndpoint(request);
                        String params = extractParams(request);
                        String method = request.get("method").asText();
                        String headers = extractHeaders(request);
                        String body = extractBody(request);

                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue("Call API");
                        row.createCell(1).setCellValue(baseUrl);
                        row.createCell(2).setCellValue(endpoint);
                        row.createCell(3).setCellValue(method);
                        row.createCell(4).setCellValue(params);
                        row.createCell(5).setCellValue(auth);
                        row.createCell(6).setCellValue(headers);
                        row.createCell(7).setCellValue(body);
                    }
                }
            }

            // Write to Excel file
            try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                workbook.write(outputStream);
            }
            workbook.close();
            System.out.println("Excel file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractBaseUrl(JsonNode request) {
        JsonNode url = request.get("url");
        String port="";
        List<String> s1 = new ArrayList<>();
        if (url != null && url.has("host")) {
            for (int i=0; i< url.get("host").size();i++) {
                s1.add(url.get("host").get(i).asText());
            }
            if(url.has("port"))
                port= ":"+url.get("port").asText();
            return url.get("protocol").asText() + "://" + String.join(".", s1) +port;
        }
        return "";
    }

    private static String extractEndpoint(JsonNode request) {
        JsonNode url = request.get("url");
        List<String> s1 = new ArrayList<>();
        if (url != null && url.has("path")) {
            for (int i=0; i< url.get("path").size();i++) {
                s1.add(url.get("path").get(i).asText());
            }
            return "/" + String.join("/", s1);
        }
        return "";
    }

    private static String extractParams(JsonNode request) {
        JsonNode url = request.get("url");
        List<String> s1 = new ArrayList<>();
        if (url != null && url.has("query")) {
            JsonNode params = url.get("query");
            for (JsonNode param : params) {
                s1.add(param.get("key").asText() + "=" + param.get("value").asText().trim());
            }
            return "?" + String.join("&", s1);
        }
        return "";
    }

    private static String extractHeaders(JsonNode request) {
        StringBuilder headers = new StringBuilder();
        JsonNode headerArray = request.get("header");
        if (headerArray != null && headerArray.isArray()) {
            for (JsonNode header : headerArray) {
                headers.append(header.get("key").asText()).append("= ").append(header.get("value").asText()).append("\n");
            }
        }

        // Extract raw language
        JsonNode body = request.get("body"); // Step 1: Get the 'body' node
        if (body != null && body.has("options")) {
            JsonNode options = body.get("options"); // Step 2: Get 'options' node
            if (options != null && options.has("raw")) {
                JsonNode rawNode = options.get("raw"); // Step 3: Get 'raw' node
                if (rawNode != null && rawNode.has("language")) {
                    String language = rawNode.get("language").asText();
                    if ("json".equalsIgnoreCase(language)) { // Check if language is 'json'
                        headers.append("Content-Type= application/json\n"); // Add Content-Type
                    }
                }
            }
        }
        return headers.toString();
    }

    private static String extractBody(JsonNode request) {
        JsonNode body = request.get("body");
        if (body != null && body.has("raw")) {
            return body.get("raw").asText();
        }
        return "";
    }
}

