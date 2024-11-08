package com.oberasoftware.robo.core.sensors;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.events.BumperEvent;
import com.oberasoftware.iot.core.robotics.sensors.DirectPort;
import com.oberasoftware.iot.core.robotics.sensors.Port;
import com.oberasoftware.iot.core.robotics.sensors.SensorDriver;
import com.oberasoftware.iot.core.robotics.sensors.TriggerValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BumperSensor extends AbstractSensor<TriggerValue> {
    private static final Logger LOG = LoggerFactory.getLogger(BumperSensor.class);

    private final String name;
    private final String portName;

    private RobotHardware robot;

    private Port port;

    public BumperSensor(String name, String portName) {
        this.name = name;
        this.portName = portName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TriggerValue getValue() {
        return null;
    }

    @Override
    public void activate(RobotHardware robot) {
        this.robot = robot;
    }

    private void notifyListeners(TriggerValue e) {
        LOG.debug("Notifying listeners of bumper event: {}", e);
        notifyListeners(new BumperEvent(robot.getName(), portName, e.getSource(), e));
    }

    @Override
    public void activate(RobotHardware robot, SensorDriver sensorDriver) {
        activate(robot);

        port = sensorDriver.getPort(portName);
        ((DirectPort<TriggerValue>) port).listen(this::notifyListeners);
    }
}
