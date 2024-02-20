package com.oktawski.iotserver.light.dtos;

import com.oktawski.iotserver.user.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LightDto {
    private int red, green, blue, intensity = 0;

    private User user;
}
