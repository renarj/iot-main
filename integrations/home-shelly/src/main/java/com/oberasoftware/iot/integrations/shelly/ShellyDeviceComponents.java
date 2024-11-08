package com.oberasoftware.iot.integrations.shelly;


import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public enum ShellyDeviceComponents {

    SHELLY_PLUG("PlusPlugS", Lists.newArrayList("switch:0")),

    SHELLY_V1_PLUG("SHPLG-S", Lists.newArrayList("relays", "meters", "temperature")),
    PRO_3EM("Pro3EM", Lists.newArrayList("em:0", "emdata:0", "temperature:0")),
    UNKNOWN("UNKNOWN", Lists.newArrayList());

    private final String typeName;
    private final List<String> components;

    ShellyDeviceComponents(String typeName, List<String> components) {
        this.typeName = typeName;
        this.components = components;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public List<String> getComponents() {
        return components;
    }

    public static ShellyDeviceComponents forTypeName(String typeName) {
        return Arrays.stream(values())
                .filter(dc -> dc.getTypeName().equalsIgnoreCase(typeName))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
