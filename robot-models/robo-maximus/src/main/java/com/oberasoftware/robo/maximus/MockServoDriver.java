package com.oberasoftware.robo.maximus;

import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.*;
import com.oberasoftware.robo.api.servo.*;
import com.oberasoftware.robo.core.ServoDataImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockServoDriver implements ServoDriver {

    private final List<String> servos = new ArrayList<>();

    public MockServoDriver(List<String> servos) {
        this.servos.addAll(servos);
    }

    @Override
    public boolean setServoSpeed(String servoId, int speed, Scale scale) {
        return false;
    }

    @Override
    public boolean setTargetPosition(String servoId, int targetPosition, Scale scale) {
        return false;
    }

    @Override
    public boolean setPositionAndSpeed(String servoId, int speed, Scale speedScale, int targetPosition, Scale positionScale) {
        return false;
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
    public boolean supportsCommand(ServoCommand servoCommand) {
        return false;
    }

    @Override
    public boolean sendCommand(ServoCommand servoCommand) {
        return false;
    }

    @Override
    public boolean setTorgue(String servoId, int limit) {
        return false;
    }

    @Override
    public boolean setTorgue(String servoId, boolean state) {
        return false;
    }

    @Override
    public boolean setTorgueAll(boolean state) {
        return false;
    }

    @Override
    public boolean setTorgueAll(boolean state, List<String> servos) {
        return false;
    }

    @Override
    public List<Servo> getServos() {
        return servos.stream().map((Function<String, Servo>) MockServo::new).collect(Collectors.toList());
    }

    @Override
    public Servo getServo(String servoId) {
        if (servos.stream().anyMatch(s -> s.equalsIgnoreCase(servoId))) {
            return new MockServo(servoId);
        }
        return null;
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
    public void activate(Robot robot, Map<String, String> properties) {

    }

    @Override
    public void shutdown() {

    }

    private class MockServo implements Servo {
        private final String servoId;

        public MockServo(String servoId) {
            this.servoId = servoId;
        }

        @Override
        public ServoData getData() {
            Map<ServoProperty, Object> m = new HashMap<>();
            m.put(ServoProperty.POSITION, 2000);
            m.put(ServoProperty.POSITION_SCALE, new Scale(0, 4096));

            return new ServoDataImpl(servoId, m);
        }

        @Override
        public void moveTo(int position, Scale scale) {

        }

        @Override
        public void setSpeed(int speed, Scale scale) {

        }

        @Override
        public void setTorgueLimit(int torgueLimit) {

        }

        @Override
        public void enableTorgue() {

        }

        @Override
        public void disableTorgue() {

        }

        @Override
        public String getId() {
            return servoId;
        }

        @Override
        public int getDeviceTypeId() {
            return 0;
        }
    }
}
