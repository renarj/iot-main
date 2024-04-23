package com.oberasoftware.robo.cloud;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.BasicCommandBuilder;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.Servo;
import com.oberasoftware.iot.core.robotics.servo.ServoCommand;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Renze de Vries
 */
@Component
@Scope("prototype")
public class RemoteServoDriver implements ServoDriver {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteServoDriver.class);

    private RobotHardware robot;

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        LOG.info("Activating remote motion engine for robot: {}", robot.getName());
        this.robot = robot;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean setServoSpeed(String servoId, int speed, Scale scale) {
        return false;
    }

    @Override
    public boolean setTargetPosition(String servoId, int targetPosition, Scale scale) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("position")
                .property("servoId", servoId)
                .property("position", Integer.toString(targetPosition))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean supportsCommand(ServoCommand servoCommand) {
        return false;
    }

    @Override
    public boolean sendCommand(ServoCommand servoCommand) {
        return false;
    }

    @Override
    public boolean setPositionAndSpeed(String servoId, int speed, Scale speedScale, int targetPosition, Scale positionScale) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("position")
                .type(CommandType.SET_STATE)
                .property("servoId", servoId)
                .property("position", Integer.toString(targetPosition))
                .property("speed", Integer.toString(speed))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands) {
        return false;
    }

    @Override
    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands, BulkPositionSpeedCommand.WRITE_MODE mode) {
        return false;
    }

    @Override
    public boolean setTorgue(String servoId, int limit) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("torgue")
                .type(CommandType.SET_STATE)
                .property("servoId", servoId)
                .property("torgue", Boolean.toString(true))
                .property("torgueLimit", Integer.toString(limit))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean setTorgue(String servoId, boolean state) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("torgue")
                .type(CommandType.SET_STATE)
                .property("servoId", servoId)
                .property("torgue", Boolean.toString(state))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean setTorgueAll(boolean state) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("torgue")
                .type(CommandType.SET_STATE)
                .property("torgue", Boolean.toString(state))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean setTorgueAll(boolean state, List<String> servos) {
        BasicCommand command = BasicCommandBuilder.create(robot.getName())
                .thing("servos").attribute("torgue")
                .type(CommandType.SET_STATE)
                .property("torgue", Boolean.toString(state))
                .property("servos", String.join(",", servos))
                .build();

        robot.getRemoteDriver().publish(command);

        return true;
    }

    @Override
    public boolean resetServos() {
        return false;
    }

    @Override
    public boolean resetServo(String servoId) {
        return false;
    }

    @Override
    public List<Servo> getServos() {
        return null;
    }

    @Override
    public Servo getServo(String servoId) {
        return null;
    }
}
