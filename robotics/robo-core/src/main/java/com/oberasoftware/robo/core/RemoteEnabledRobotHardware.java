package com.oberasoftware.robo.core;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.Capability;
import com.oberasoftware.iot.core.robotics.RemoteDriver;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.commands.CommandListener;
import com.oberasoftware.iot.core.robotics.commands.RobotCommand;
import com.oberasoftware.iot.core.robotics.events.RobotValueEvent;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Renze de Vries
 */
public class RemoteEnabledRobotHardware implements RobotHardware, CommandListener, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteEnabledRobotHardware.class);

    private final RobotHardware localRobot;
    private final RemoteDriver remoteDriver;
    private final boolean isRemote;

    public RemoteEnabledRobotHardware(RemoteDriver remoteDriver, RobotHardware localRobot, boolean isRemote) {
        this.localRobot = localRobot;
        this.remoteDriver = remoteDriver;
        this.isRemote = isRemote;
        remoteDriver.register(this);

        this.localRobot.listen(this);
    }

    @Override
    public String getName() {
        return localRobot.getName();
    }

    @Override
    public void publish(Event robotEvent) {
        localRobot.publish(robotEvent);
    }

    @Override
    public void listen(EventHandler robotEventHandler) {
        this.localRobot.listen(robotEventHandler);
    }

    @Override
    public ServoDriver getServoDriver() {
        return localRobot.getServoDriver();
    }

    @Override
    public List<Capability> getCapabilities() {
        return localRobot.getCapabilities();
    }

    @Override
    public <T extends Capability> T getCapability(Class<T> capabilityClass) {
        return localRobot.getCapability(capabilityClass);
    }

    @Override
    public boolean isRemote() {
        return this.isRemote;
    }

    @Override
    public RemoteDriver getRemoteDriver() {
        return remoteDriver;
    }

    @Override
    public void shutdown() {
        this.localRobot.shutdown();
    }

    @Override
    public void receive(RobotCommand command) {
        LOG.info("Received a remote robot command: {}", command);
    }

    @EventSubscribe
    public void receiveRobotEvent(RobotValueEvent robotEvent) {
        LOG.debug("Received a robot event: {}", robotEvent);
        remoteDriver.publish(robotEvent);
    }
}
