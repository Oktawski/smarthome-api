package com.oktawski.iotserver.utilities;

import com.oktawski.iotserver.superclasses.WifiDevice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceHelper {

    public <T extends WifiDevice> boolean isIpUnique(List<T> list, String ip){
        return list.stream()
                .anyMatch(v -> v.getIp().equals(ip));
    }

}
