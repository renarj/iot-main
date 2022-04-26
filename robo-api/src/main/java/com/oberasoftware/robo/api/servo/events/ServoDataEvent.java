package com.oberasoftware.robo.api.servo.events;

import com.oberasoftware.robo.api.events.RobotValueEvent;
import com.oberasoftware.robo.api.servo.ServoData;
import com.oberasoftware.robo.api.servo.ServoProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Renze de Vries
 */
public abstract class ServoDataEvent implements RobotValueEvent {
    public static final String SERVO_PATH_PREFIX = "servos.";
    private final String servoId;
    private final ServoData updateData;

    protected ServoDataEvent(String servoId, ServoData updateData) {
        this.servoId = servoId;
        this.updateData = updateData;
    }

    @Override
    public String getSourcePath() {
        return SERVO_PATH_PREFIX + getServoId();
    }

    @Override
    public Set<String> getAttributes() {
        return getServoData().getKeys().stream().map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    public Map<String, ?> getValues() {
        Map<String, ?> values = new HashMap<>();
        getAttributes().forEach(a -> values.put(a, getValue(a)));
        return values;
    }

    @Override
    public <T> T getValue(String attribute) {
        return getServoData().getValue(ServoProperty.valueOf(attribute));
    }

    public String getServoId() {
        return servoId;
    }

    public ServoData getServoData() {
        return updateData;
    }

    @Override
    public String toString() {
        return "ServoUpdateEvent{" +
                "servoId='" + servoId + '\'' +
                ", updateData=" + updateData +
                '}';
    }
}
