package com.oberasoftware.home.rest.model;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renarj
 */
public class BasicRestCommand implements BasicCommand {
    private String thingId;
    private CommandType commandType;

    private String controllerId;

    private Map<String, String> properties = new HashMap<>();

    @Override
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
        return "BasicRESTCommand{" +
                "thingId='" + thingId + '\'' +
                ", commandType='" + commandType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
