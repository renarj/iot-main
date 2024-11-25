package com.oberasoftware.robot.nao;


import com.oberasoftware.iot.core.robotics.events.RobotEvent;

import java.util.List;

/**
 * @author Renze de Vries
 */
public class ListValueEvent implements RobotEvent {
    private final List<Object> values;
    private final String source;

    public ListValueEvent(List<Object> values, String source) {
        this.values = values;
        this.source = source;
    }

    public List<Object> getValues() {
        return values;
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

    @Override
    public String toString() {
        return "ListValueEvent{" +
                "values=" + values +
                ", source='" + source + '\'' +
                '}';
    }
}
