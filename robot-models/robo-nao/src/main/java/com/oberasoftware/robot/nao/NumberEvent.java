package com.oberasoftware.robot.nao;


import com.oberasoftware.iot.core.robotics.events.RobotEvent;

/**
 * @author Renze de Vries
 */
public class NumberEvent implements RobotEvent {
    private final String source;
    private final int number;

    public NumberEvent(String source, int number) {
        this.source = source;
        this.number = number;
    }

    @Override
    public String getControllerId() {
        return null;
    }

    @Override
    public String getItemId() {
        return null;
    }

    @Override
    public String getAttribute() {
        return source;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "NumberEvent{" +
                "source='" + source + '\'' +
                ", number=" + number +
                '}';
    }
}
