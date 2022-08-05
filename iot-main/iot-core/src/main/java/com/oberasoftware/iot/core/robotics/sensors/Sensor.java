package com.oberasoftware.iot.core.robotics.sensors;

import com.oberasoftware.iot.core.robotics.Capability;
import com.oberasoftware.iot.core.robotics.Robot;

/**
 * @author Renze de Vries
 */
public interface Sensor extends Capability {

    default String getName() {
        return this.getClass().getName();
    }

    default void activate(Robot robot, SensorDriver sensorDriver) {
        //no activiation needed by default
        activate(robot);
    }

    void activate(Robot robot);
}
