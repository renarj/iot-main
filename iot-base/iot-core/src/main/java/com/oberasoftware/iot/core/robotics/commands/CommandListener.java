package com.oberasoftware.iot.core.robotics.commands;

/**
 * @author Renze de Vries
 */
public interface CommandListener<T extends RobotCommand> {
    void receive(T command);
}
