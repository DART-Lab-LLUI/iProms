package com.llui.iproms.Enum;

public enum QuestionnaireStatus {
    NOT_STARTED("Not started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    SKIPPED("Skipped");

    private final String status;

    QuestionnaireStatus(String status) {
        this.status = status;
    }
}
