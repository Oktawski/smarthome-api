package com.oktawski.iotserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class SimpleResult {
    protected String message;

    protected Status status;

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public static SimpleResult success(String message) {
        return new SimpleResult(message, Status.SUCCESS);
    }

    public static SimpleResult warning(String message) {
        return new SimpleResult(message, Status.WARNING);
    }

    public static SimpleResult error(String message) {
        return new SimpleResult(message, Status.ERROR);
    }
}
