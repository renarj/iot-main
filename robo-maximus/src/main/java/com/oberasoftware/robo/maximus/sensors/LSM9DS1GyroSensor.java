package com.oberasoftware.robo.maximus.sensors;

import com.google.common.collect.Sets;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.sensors.MultiValueSensor;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.oberasoftware.robo.maximus.sensors.TeensySensorDriver.*;

public class LSM9DS1GyroSensor implements MultiValueSensor<DoubleValue> {

    private double heading;
    private double roll;
    private double pitch;

    @Override
    public String getName() {
        return "LSM9DS1";
    }

    @Override
    public DoubleValue getValue(String attribute) {
        switch(attribute) {
            case LSM_9_DS_1_HEADING:
                return new DoubleValue(heading);
            case LSM_9_DS_1_PITCH:
                return new DoubleValue(pitch);
            case LSM_9_DS_1_ROLL:
                return new DoubleValue(roll);
            default:
                return new DoubleValue(0.0);
        }
    }

    @Override
    public Set<String> getAttributes() {
        return Sets.newHashSet(LSM_9_DS_1_HEADING, LSM_9_DS_1_PITCH, LSM_9_DS_1_ROLL);
    }

    @Override
    public Map<String, DoubleValue> getValues() {
        Map<String, DoubleValue> values = new HashMap<>();
        values.put(LSM_9_DS_1_HEADING, new DoubleValue(heading));
        values.put(LSM_9_DS_1_ROLL, new DoubleValue(roll));
        values.put(LSM_9_DS_1_PITCH, new DoubleValue(pitch));

        return values;
    }

    @Override
    public void activate(Robot robot) {
        TeensySensorDriver sensorDriver = robot.getCapability(TeensySensorDriver.class);
        if(sensorDriver != null) {
            sensorDriver.getPort(LSM_9_DS_1_HEADING).listen(e -> {
                this.heading = e.getRaw();
                robot.publish(new SensorDataImpl(new DoubleValue(e.getRaw()), LSM_9_DS_1_HEADING));
            });
            sensorDriver.getPort(LSM_9_DS_1_PITCH).listen(e -> {
                this.pitch = e.getRaw();
                robot.publish(new SensorDataImpl(new DoubleValue(e.getRaw()), LSM_9_DS_1_PITCH));
            });
            sensorDriver.getPort(LSM_9_DS_1_ROLL).listen(e -> {
                this.roll = e.getRaw();
                robot.publish(new SensorDataImpl(new DoubleValue(e.getRaw()), LSM_9_DS_1_ROLL));
            });
        }
    }
}
