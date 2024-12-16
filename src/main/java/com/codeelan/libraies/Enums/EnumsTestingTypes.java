package com.codeelan.libraies.Enums;

import lombok.Getter;

@Getter
public enum EnumsTestingTypes {
    ENUMSTESTINGTYPES("UI, API");
    private final String text;

    EnumsTestingTypes(String text) {
        this.text = text;
    }

}