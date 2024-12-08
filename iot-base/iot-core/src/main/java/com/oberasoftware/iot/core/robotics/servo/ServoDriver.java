package com.oberasoftware.iot.core.robotics.servo;

import com.oberasoftware.iot.core.robotics.ActivatableCapability;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;

import java.util.List;
import java.util.Map;

/**
 * @author Renze de Vries
 */
public interface ServoDriver extends ActivatableCapability {


    boolean setServoSpeed(String servoId, int speed, Scale scale);

    boolean setTargetPosition(String servoId, int targetPosition, Scale scale);

    boolean setPositionAndSpeed(String servoId, int speed, Scale speedScale, int targetPosition, Scale positionScale);

    boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands);

    boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands, BulkPositionSpeedCommand.WRITE_MODE mode);

    boolean supportsCommand(ServoCommand servoCommand);

    boolean sendCommand(ServoCommand servoCommand);

    boolean setTorgue(String servoId, int limit);

    boolean setTorgue(String servoId, boolean state);

    boolean setTorgueAll(boolean state);

    boolean setTorgueAll(boolean state, List<String> servos);

    boolean resetServos();

    boolean resetServo(String servoId);

    List<Servo> getServos();

    Servo getServo(String servoId);

    boolean lock();

    void unlock();
}
