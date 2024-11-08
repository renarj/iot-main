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

public class Ina260CurrentSensor implements MultiValueSensor<DoubleValue> {

    private static final int UNIT_DIVISION = 1000;
    private Double voltage = 0.0;
    private Double current = 0.0;
    private Double power = 0.0;

    private final String controllerId;
    private final String thingId;

    public Ina260CurrentSensor(String controllerId, String thingId) {
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
            case INA_260_CURRENT:
                return new DoubleValue(current);
            case INA_260_VOLTAGE:
                return new DoubleValue(voltage);
            case INA_260_POWER:
                return new DoubleValue(power);
            default:
                return new DoubleValue(0.0);
        }
    }

    @Override
    public Set<String> getAttributes() {
        return Sets.newHashSet(INA_260_CURRENT, INA_260_POWER, INA_260_VOLTAGE);
    }

    @Override
    public Map<String, DoubleValue> getValues() {
        Map<String, DoubleValue> values = new HashMap<>();
        values.put(INA_260_CURRENT, new DoubleValue(current));
        values.put(INA_260_VOLTAGE, new DoubleValue(voltage));
        values.put(INA_260_POWER, new DoubleValue(power));

        return values;
    }

    @Override
    public void activate(RobotHardware robot) {
        ESP32SensorDriver sensorDriver = robot.getCapability(ESP32SensorDriver.class);
        if(sensorDriver != null) {
            sensorDriver.getPort(INA_260_CURRENT).listen(e -> {
                this.current = e.getRaw() / UNIT_DIVISION;
                robot.publish(new SensorDataImpl(controllerId, thingId, INA_260_CURRENT, new ValueImpl(VALUE_TYPE.NUMBER, this.current)));
            });
            sensorDriver.getPort(INA_260_VOLTAGE).listen(e -> {
                this.voltage = e.getRaw() / UNIT_DIVISION;

                robot.publish(new SensorDataImpl(controllerId, thingId, INA_260_VOLTAGE, new ValueImpl(VALUE_TYPE.NUMBER, this.voltage)));
            });
            sensorDriver.getPort(INA_260_POWER).listen(e -> {
                this.power = e.getRaw() / UNIT_DIVISION;
                robot.publish(new SensorDataImpl(controllerId, thingId, INA_260_POWER, new ValueImpl(VALUE_TYPE.NUMBER, this.power)));
            });
        }
    }
}
