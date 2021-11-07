package com.oktawski.iotserver.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Builder
public class BasicResponse<T extends Object> {

    @JsonProperty("object")
    protected T object;

    @JsonProperty("msg")
    protected String msg;

    public BasicResponse(T t, String msg) {
        this.object = t;
        this.msg = msg;
    }
}
