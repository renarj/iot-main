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

    private CommandType commandType;

    private Map<String, String> attributes = new HashMap<>();

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
    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    @Override
    public String toString() {
        return "BasicCommandImpl{" +
                "controllerId='" + controllerId + '\'' +
                ", thingId='" + thingId + '\'' +
                ", commandType=" + commandType +
                ", attributes=" + attributes +
                '}';
    }
}
