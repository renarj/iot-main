package com.oberasoftware.iot.core.commands;

import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import com.oberasoftware.iot.core.commands.impl.CommandType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Renze de Vries
 */
public class BasicCommandBuilder {

    private final Map<String, String> properties = new HashMap<>();
    private final String controllerId;

    private String thing;
    private CommandType type;

    private String attribute;

    private BasicCommandBuilder(String controllerId) {
        this.controllerId = controllerId;
    }

    public static BasicCommandBuilder create(String controllerId) {
        return new BasicCommandBuilder(controllerId);
    }

    public BasicCommandBuilder thing(String thingId) {
        this.thing = thing;
        return this;
    }

    public BasicCommandBuilder type(CommandType type) {
        this.type = type;
        return this;
    }

    public BasicCommandBuilder property(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public BasicCommandBuilder attribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    public BasicCommand build() {
        BasicCommandImpl command = new BasicCommandImpl();
        command.setControllerId(controllerId);
        command.setCommandType(type);
        command.setThingId(thing);
        command.setAttributes(properties);

        return command;
    }
}
