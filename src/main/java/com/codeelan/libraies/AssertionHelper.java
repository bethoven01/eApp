package com.codeelan.libraies;

import io.restassured.response.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
//import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AssertionHelper {

    private static final org.apache.logging.log4j.Logger Log = LogManager.getLogger(PageObjectManager.class);

//    public static void assertAll(String input, String output) {
//        try {
//            JSONAssert.assertEquals(input, output, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void assertAll(String input, String output, String strict) {
//        try {
//            JSONAssert.assertEquals(input, output, true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public static void assertValue(String actual, String expected) {
        String message = "Expected value: " + expected + " not matched with actual value: " + actual;
        assertEquals(actual, expected, message);
    }

    public static void assertFieldValue(String actual, String expected, String field) {
        String message = "Field: '" + bold(field) + "'" + "\n\n" +
                " | Expected value: '" + bold(expected) + "' not matched with actual value: '"
                + bold(actual) + "'" + "\n\n";
        assertEquals(actual, expected, message);
        Log.info("Field: '" + bold(field) + "'|" + "\n\n" + " Actual value: '"
                + bold(actual) + "' is equal to expected value: '"
                + bold(expected) + "'" + "\n\n");
    }
    public static void assertString(String actual, String expected, String error_message) {
        assertEquals(actual, expected, error_message);
    }

    public static void assertInt(Integer actual, Integer expected, String field) {
        String message = "Field: '" + bold(field) + "'" + " |Actual value: '" + bold(actual.toString()) +
                "' is not equal to expected value: '" + bold(expected.toString()) + "' |" + "\n\n";
        assertEquals(actual, expected, message);

        Log.info("Field: '" + bold(field) + "'" + " Actual value: '" + bold(actual.toString()) +
                "' is equal to expected value: '"
                + bold(expected.toString()) + "'" + "\n\n");
    }

    public static void assertLong(Long actual, Long expected, String field) {
        String message = "Field: '" + bold(field) + "'" + " |Actual value: '" + bold(actual.toString()) +
                "' is not equal to expected value: '" + bold(expected.toString()) + "' |" + "\n\n";
        assertEquals(actual, expected, message);

        Log.info("Field: '" + bold(field) + "'" + " Actual value: '" + bold(actual.toString()) +
                "' is equal to expected value: '"
                + bold(expected.toString()) + "'" + "\n\n");
    }

    public static void assertContains(String actual, String expected) {
        String message = "Actual value: '" + actual + "' doesn't contains expected value: '" + expected + "'";
        assertTrue(actual.contains(expected), message);
    }

    public static void assertContains(String actual, String expected, String field) {
        String message = "Field: '" + bold(field) + "'" + " |Actual value: '" + bold(actual) +
                "' doesn't contains expected value: '" + bold(expected) + "' |" + "\n\n";
        assertTrue(actual.contains(expected), message);

        Log.info("Field: '" + bold(field) + "'|Actual value: '" + bold(actual) + "' contains the expected value: '"
                + bold(expected) + "'");
    }

    public static void assertNotNull(String actual, String field) {
        String message = "Field: '" + bold(field) + "'" + "\n\n" + " |Expected not null however, Actual value: " +
                bold(actual) + " is null| " + "\n\n";
        assertTrue(!actual.isEmpty(), message);
        Log.info("Field: " + bold(field) + " value is '" + bold("not null") + "' as expected");
    }

    public static void assertForNull(String actual, String field) {
        String message = "Field: '" + bold(field) + "'" + "\n\n" + " Expected " +
                bold("null") + " however, Actual value: " +
                bold(actual);
        assertNull(actual, message + "\n\n" + new Exception().getMessage());
        Log.info("Field:" + bold(field) + " value is null as expected");
    }

    public static void assertValue(String input, String key, String value) {
        try {
            JSONObject inputJson = new JSONObject(input);
            String Key = inputJson.get(key).toString();
            assertEquals(Key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void assertNullArray(String[] array, String field) {
        String message = "Field: '" + field + "'" + " Expected null however, Actual value is not null " + "\n\n";
        assertTrue(ArrayUtils.isEmpty(array), message);
    }

    public static void assertEmpty(String actual, String field) {
        String message = "Field: '" + bold(field) + "'" + "\n\n" +
                " Expected " + bold("empty") + " however, Actual value: " +
                bold(actual);
        assertTrue(actual.isEmpty(), message + "\n\n" + new Exception().getMessage());
        Log.info("Field:" + bold(field) + " value is blank as expected");
    }

    public static void assertTime(Response response, String expected, String field) {
        Long actual = response.getTime();
        String message = "Field: '" + bold(field) + "'" + "\n\n" +
                "Actual value: '" + actual + "' is more than expected value: '"
                + bold(expected) + "'" + "\n\n";
        if (actual > Long.parseLong(expected)) {
            Log.warn(message);
        } else {
            Log.info("Field: '" + bold(field) + "'" + "\n\n" +
                    "Actual value: '" + actual + "' is less than expected value: '"
                    + bold(expected) + "' as exoected" + "\n\n");
        }
    }

    public static void assertArrayContains(String actual, String[] expected, String field) {
        String message = "Field: '" + bold(field) + "'" + " |Actual value: '" + bold(actual) +
                "' doesn't contains expected value: '" + bold(expected.toString()) + "' |" + "\n\n";
        assertTrue(Arrays.asList(expected).contains(actual), message);
        Log.info("Field: '" + bold(field) + "'|Actual value: '" + bold(actual) + "' contains the expected value: '"
                + bold(expected.toString()) + "'");
    }

    public static void assertCondition(boolean condition, String field) {
        String message = "Field: '" + bold(field) + "'" + " |is false";
        assertTrue(condition, message);
        Log.info("Field: '" + bold(field) + "is true");
    }

    public static String bold(String text) {
        return new StringBuffer().append("<b>").append(text).append("</b>").toString();
    }
}
