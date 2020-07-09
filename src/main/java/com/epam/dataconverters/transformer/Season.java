package com.epam.dataconverters.transformer;

public enum Season {
    WINTER("mini-Winter"),
    SUMMER;

    private String value;

    private Season () {}

    Season(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
