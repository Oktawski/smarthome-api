package com.oktawski.iotserver.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicResponse<T extends Object> {

    @JsonProperty("object")
    protected T t;
    protected String msg;

    public BasicResponse() {
    }

    public BasicResponse(T t, String msg) {
        this.t = t;
        this.msg = msg;
    }

    public T getObject() {
        return t;
    }

    public void setObject(T t) {
        this.t = t;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
