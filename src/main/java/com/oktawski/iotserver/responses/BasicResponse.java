package com.oktawski.iotserver.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicResponse<T extends Object> {

    @JsonProperty("object")
    protected T t;

    @JsonProperty("msg")
    protected String msg;

    public BasicResponse() {
    }

    public BasicResponse(T t, String msg) {
        this.t = t;
        this.msg = msg;
    }

    public void setObject(T t) {
        this.t = t;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
