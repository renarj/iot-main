package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.ThingCommand;

public class ConfigUpdatedCommand implements ThingCommand {
    private final String controllerId;
    private final String thingId;

    public ConfigUpdatedCommand(String controllerId, String thingId) {
        this.controllerId = controllerId;
        this.thingId = thingId;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getThingId() {
        return thingId;
    }
}
