package com.oberasoftware.iot.integrations.shelly;


import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public enum ShellyDeviceComponents {

    SHELLY_PLUG("PlusPlugS", Lists.newArrayList("switch:0")),
    PRO_3EM("Pro3EM", Lists.newArrayList("em:0", "emdata:0", "temperature:0")),
    UNKNOWN("UNKNOWN", Lists.newArrayList());

    private String appName;
    private List<String> components;

    ShellyDeviceComponents(String appName, List<String> components) {
        this.appName = appName;
        this.components = components;
    }

    public String getAppName() {
        return this.appName;
    }

    public List<String> getComponents() {
        return components;
    }

    public static ShellyDeviceComponents forAppName(String appName) {
        return Arrays.stream(values())
                .filter(dc -> dc.getAppName().equalsIgnoreCase(appName))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
