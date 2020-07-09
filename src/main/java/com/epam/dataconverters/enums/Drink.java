package com.epam.dataconverters.enums;

public class Drink {

    private DrinkType type;

    public Drink(DrinkType type) {
        this.type = type;
    }

    public enum Tea implements DrinkType {
        CHINA,
        INDIAN,
        KAZAKH
    }

    public enum Coffee implements DrinkType {
        AFRICAN,
        BRAZILIAN
    }

    @Override
    public String toString() {
        return "Drink{" + type.getClass().getSimpleName() + "." +
                "type=" + type +
                '}';
    }
}
