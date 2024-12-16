package com.codeelan.libraies;

import lombok.Getter;

@Getter
public enum EnumsCommon {
    ABSOLUTE_CLIENTFILES_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\Client\\"),
    FIELD("Common Tag"),
    JURISDICTION("Jurisdiction"),
    E2ETITLE("Title"),
    E2ETESTDATA("Test Data"),
    TOVISIBLE("ToVisible"),
    TOCLICKABLE("ToClickable"),
    TOINVISIBLE("ToInvisible"),
    TESTDATA_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\"),
    ABSOLUTE_FILES_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\"),
    RESPONSE_FILES_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\Responses\\"),
    EXPECTEDRESPONSE_FILES_PATH("testdata\\Responses\\"),
    REUSABLE_FILES_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\ReusableMethods\\"),
    FIREFOXBROWSER("Firefox"),
    FEATUREFILESPATH(System.getProperty("user.dir") + "/src/test/resources/features/"),
    RUNNERFILESPATH(System.getProperty("user.dir") + "/src/test/java/com/codeelan/runner/"),
    EXTENSION_FILES_PATH(System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\Extensions\\");

    private final String text;

    EnumsCommon(String text) {
        this.text = text;
    }
}
