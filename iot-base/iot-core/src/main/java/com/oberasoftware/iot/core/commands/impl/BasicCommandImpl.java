package com.oberasoftware.iot.core.commands.impl;


import com.oberasoftware.iot.core.commands.BasicCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class BasicCommandImpl implements BasicCommand {
    private String controllerId;
    private String thingId;
    private String commandType;

    private Map<String, String> properties = new HashMap<>();

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    @Override
    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String property) {
        return properties.get(property);
    }

    @Override
    public String toString() {
        return "BasicCommandImpl{" +
                "controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", commandType='" + commandType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
