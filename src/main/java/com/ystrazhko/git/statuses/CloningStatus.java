package com.ystrazhko.git.statuses;


public enum CloningStatus {
    SUCCESSFUL("successful"),
    FAILED("failed");

    private String message;

    CloningStatus(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
