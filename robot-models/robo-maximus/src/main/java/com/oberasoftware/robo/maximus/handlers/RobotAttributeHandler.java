package com.oberasoftware.robo.maximus.handlers;

import com.oberasoftware.iot.core.commands.ThingValueCommand;

import java.util.Set;

public interface RobotAttributeHandler {
    Set<String> getSupportedAttributes();

    void handle(String attribute, ThingValueCommand command);
}
