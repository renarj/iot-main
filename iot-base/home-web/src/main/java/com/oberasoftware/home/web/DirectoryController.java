package com.oberasoftware.home.web;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/web")
public class DirectoryController {
    @Value("${thing-svc.baseUrl}")
    private String thingServiceUrl;

    @Value("${state-svc.baseUrl}")
    private String stateServiceUrl;

    @RequestMapping("/serviceLocations")
    public Map<String, String> getServiceLocations() {
        return new ImmutableMap.Builder<String, String>()
                .put("thingSvcUrl", thingServiceUrl)
                .put("stateSvcUrl", stateServiceUrl)
                .build();
    }
}
