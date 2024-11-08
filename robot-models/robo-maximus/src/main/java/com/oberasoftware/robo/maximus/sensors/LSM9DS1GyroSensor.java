package com.oberasoftware.robo.maximus.sensors;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.sensors.MultiValueSensor;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.oberasoftware.robo.maximus.sensors.ESP32SensorDriver.*;

public class LSM9DS1GyroSensor implements MultiValueSensor<DoubleValue> {

    private double heading;
    private double roll;
    private double pitch;

    private final String controllerId;
    private final String thingId;

    public LSM9DS1GyroSensor(String controllerId, String thingId) {
        this.controllerId = controllerId;
        this.thingId = thingId;
    }

    @Override
    public String getName() {
        return thingId;
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
    public void activate(RobotHardware robot) {
        ESP32SensorDriver sensorDriver = robot.getCapability(ESP32SensorDriver.class);
        if(sensorDriver != null) {
            sensorDriver.getPort(LSM_9_DS_1_HEADING).listen(e -> {
                this.heading = e.getRaw();
                robot.publish(new SensorDataImpl(controllerId, thingId, LSM_9_DS_1_HEADING, new ValueImpl(VALUE_TYPE.NUMBER, e.getRaw())));
            });
            sensorDriver.getPort(LSM_9_DS_1_PITCH).listen(e -> {
                this.pitch = e.getRaw();
                robot.publish(new SensorDataImpl(controllerId, thingId, LSM_9_DS_1_PITCH, new ValueImpl(VALUE_TYPE.NUMBER, e.getRaw())));
            });
            sensorDriver.getPort(LSM_9_DS_1_ROLL).listen(e -> {
                this.roll = e.getRaw();
                robot.publish(new SensorDataImpl(controllerId, thingId, LSM_9_DS_1_ROLL, new ValueImpl(VALUE_TYPE.NUMBER, e.getRaw())));
            });
        }
    }
}
