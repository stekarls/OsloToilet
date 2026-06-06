package com.app.oslotoilet.enums;

public enum Contribution {
    APPROVED(100),
    REVIEW(20);

    private final int value;

    Contribution(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
