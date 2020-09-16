package com.oktawski.iotserver;

import com.oktawski.iotserver.user.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmartHomeServerApplicationTests {

    @Autowired
    private UserController controller;

    @Test
    public void contextLoads() throws Exception{
        assertThat(controller).isNotNull();
    }
}
