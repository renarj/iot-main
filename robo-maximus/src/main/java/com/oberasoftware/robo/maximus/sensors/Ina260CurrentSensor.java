package com.oberasoftware.robo.maximus.sensors;

import com.google.common.collect.Sets;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.sensors.MultiValueSensor;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.oberasoftware.robo.maximus.sensors.TeensySensorDriver.*;

public class Ina260CurrentSensor implements MultiValueSensor<DoubleValue> {

    private static final int UNIT_DIVISION = 1000;
    private Double voltage = 0.0;
    private Double current = 0.0;
    private Double power = 0.0;

    @Override
    public String getName() {
        return "ina260";
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
    public void activate(Robot robot) {
        TeensySensorDriver sensorDriver = robot.getCapability(TeensySensorDriver.class);
        sensorDriver.getPort(INA_260_CURRENT).listen(e -> {
            this.current = e.getRaw() / UNIT_DIVISION;
            robot.publish(new SensorDataImpl(new DoubleValue(this.current), INA_260_CURRENT));
        });
        sensorDriver.getPort(INA_260_VOLTAGE).listen(e -> {
            this.voltage = e.getRaw() / UNIT_DIVISION;

            robot.publish(new SensorDataImpl(new DoubleValue(this.voltage), INA_260_VOLTAGE));
        });
        sensorDriver.getPort(INA_260_POWER).listen(e -> {
            this.power = e.getRaw() / UNIT_DIVISION;
            robot.publish(new SensorDataImpl(new DoubleValue(this.power), INA_260_POWER));
        });

    }
}
