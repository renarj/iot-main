package com.oberasoftware.robo.api.sensors;

import com.oberasoftware.robo.api.Capability;
import com.oberasoftware.robo.api.Robot;

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
