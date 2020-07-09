package com.epam.dataconverters.enums;

public enum Seasons {
    WINTER,
    SPRING,
    SUMMER("Little summer"),
    AUTUMN;

    private String value;

    private Seasons(){};

    Seasons(String s) {
        value = s;
    }

    public String getValue() {return value;};

}
