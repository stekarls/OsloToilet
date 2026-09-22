package com.app.oslotoilet.enums;

public enum ContributionPoints {
    APPROVED(100),
    REVIEW(20),
    ERROR_REPORT_FIXED(30);

    private final int value;

    ContributionPoints(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
