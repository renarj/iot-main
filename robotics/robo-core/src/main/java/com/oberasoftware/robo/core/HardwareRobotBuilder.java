package com.oberasoftware.robo.core;

import com.oberasoftware.base.event.EventBus;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.robotics.*;
import com.oberasoftware.iot.core.robotics.motion.MotionResource;
import com.oberasoftware.iot.core.robotics.sensors.Sensor;
import com.oberasoftware.iot.core.robotics.sensors.SensorDriver;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Renze de Vries
 */
public class HardwareRobotBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(HardwareRobotBuilder.class);

    private String robotName;

    private final ApplicationContext context;
    private final EventBus eventBus;

    private List<SensorHolder> sensors = new ArrayList<>();
    private List<CapabilityHolder> capabilities = new ArrayList<>();
    private boolean isRemote = false;

    public HardwareRobotBuilder(String robotName, ApplicationContext context) {
        this.context = context;
        this.robotName = robotName;
        this.eventBus = context.getBean(LocalEventBus.class);
    }

    public HardwareRobotBuilder(ApplicationContext context) {
        this.context = context;
        this.eventBus = context.getBean(LocalEventBus.class);
    }

    public HardwareRobotBuilder name(String name) {
        this.robotName = name;
        return this;
    }

    public String getName() {
        return this.robotName;
    }

    public HardwareRobotBuilder servoDriver(ServoDriver servoDriver, Map<String, String> properties) {
        return addCapability(servoDriver, properties);
    }

    public HardwareRobotBuilder servoDriver(Class<? extends ServoDriver> servoDriver) {
        return servoDriver(context.getBean(servoDriver), new HashMap<>());
    }

    public HardwareRobotBuilder servoDriver(Class<? extends ServoDriver> servoDriverClass, Map<String, String> properties) {
        return servoDriver(context.getBean(servoDriverClass), properties);
    }

    public HardwareRobotBuilder remote(Class<? extends RemoteDriver> remoteConnector, boolean isRemote) {
        RemoteDriver remoteDriver = context.getBean(remoteConnector);
        Map<String, String> properties = new HashMap<>();
        this.isRemote = isRemote;
        return addCapability(remoteDriver, properties);
    }

    public HardwareRobotBuilder remote(Class<? extends RemoteDriver> remoteConnector) {
        return remote(remoteConnector, false);
    }

    public HardwareRobotBuilder capability(Class<? extends Capability> capabilityClass) {
        return capability(capabilityClass, new HashMap<>());
    }

    public HardwareRobotBuilder capability(Class<? extends Capability> capabilityClass, Map<String, String> properties) {
        Capability capability = context.getBean(capabilityClass);
        return addCapability(capability, properties);
    }

    private HardwareRobotBuilder addCapability(Capability capability, Map<String, String> properties) {
        if(!capabilities.stream().filter(c -> c.getCapability().equals(capability)).findFirst().isPresent()) {
            capabilities.add(new CapabilityHolder(capability, properties));
        }

        return this;
    }

    public HardwareRobotBuilder sensor(Sensor sensor, Class<? extends SensorDriver> sensorDriverClass) {
        SensorDriver driver = null;
        if(sensorDriverClass != null) {
            driver = context.getBean(sensorDriverClass);
            addCapability(driver, new HashMap<>());
        }

        this.sensors.add(new SensorHolder(sensor, driver, eventBus));
        return this;
    }

    public RobotHardware build() {
        RobotHardware robot = buildRobot();
        context.getBean(RobotRegistry.class).register(robot);

        return robot;
    }

    private RobotHardware buildRobot() {
        LOG.info("Creating robot base system");
        GenericRobotHardware robot = new GenericRobotHardware(robotName, isRemote, eventBus, capabilities, sensors);
        robot.initialize();

        RemoteDriver remoteDriver = robot.getRemoteDriver();
        if(remoteDriver != null) {
            LOG.info("Remote robot control is enabled");
            return new RemoteEnabledRobotHardware(remoteDriver, robot, isRemote);
        } else {
            LOG.info("Robot construction finished");
            return robot;
        }
    }

}
