package com.oktawski.iotserver.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
public class BasicResponse<T> {

    @JsonProperty("object")
    protected T object;

    @JsonProperty("msg")
    protected String msg;

    private BasicResponse(T t, String msg) {
        this.object = t;
        this.msg = msg;
    }
}
