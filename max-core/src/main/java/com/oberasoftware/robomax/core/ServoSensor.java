package com.oberasoftware.robomax.core;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.sensors.DirectPort;
import com.oberasoftware.robo.api.sensors.SensorDriver;
import com.oberasoftware.robo.core.sensors.AbstractSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Renze de Vries
 */
public class ServoSensor extends AbstractSensor<PositionValue> {
    private static final Logger LOG = LoggerFactory.getLogger(ServoSensor.class);

    private final String name;
    private final String portName;

    public ServoSensor(String name, String portName) {
        this.name = name;
        this.portName = portName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PositionValue getValue() {
        return null;
    }

    @Override
    public void activate(Robot robot) {
    }

    @Override
    public void activate(Robot robot, SensorDriver sensorDriver) {
        if(sensorDriver instanceof ServoSensorDriver) {
            LOG.debug("Activating servo port: {}", portName);
            DirectPort<PositionValue> port = ((ServoSensorDriver) sensorDriver).getPort(portName);
            port.listen(e -> notifyListeners(new ServoPositionEvent(robot.getName(), name, e.getServoId(), e)));
        }
    }
}
