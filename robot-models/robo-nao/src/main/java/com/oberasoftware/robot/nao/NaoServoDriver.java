package com.oberasoftware.robot.nao;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.servo.Servo;
import com.oberasoftware.iot.core.robotics.servo.ServoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oberasoftware.robot.nao.NaoUtil.safeExecuteTask;


/**
 * @author Renze de Vries
 */
@Component
public class NaoServoDriver {
    private static final Logger LOG = LoggerFactory.getLogger(NaoServoDriver.class);

    @Autowired
    private NaoSessionManager sessionManager;

    private ALMotion alMotion;

    public void activate(RobotHardware robot, Map<String, String> properties) {
        try {
            Session session = sessionManager.getSession();
            alMotion = new ALMotion(session);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    public boolean setServoSpeed(String servoId, int speed) {
        return false;
    }

    public boolean setTargetPosition(String servoId, int targetPosition) {
        float r = toRadial(targetPosition);
        LOG.info("Setting servo: {} to radial: {}", servoId, r);
        safeExecuteTask(() -> alMotion.setAngles(servoId, r, 0.2f));

        return true;
    }

    public static float toRadial(int position) {
        if(position > 512 && position <= 1024) {
            int remainder = position - 512;
            return -(1 / (float)512) * remainder;
        } else if(position >= 0 && position <= 512) {
            int remainder = 512 - position;
            return (1 / (float)512) * remainder;
        } else {
            throw new RuntimeException("Invalid input");
        }
    }

    public boolean supportsCommand(ServoCommand servoCommand) {
        return false;
    }

    public boolean sendCommand(ServoCommand servoCommand) {
        return false;
    }

    public boolean setPositionAndSpeed(String servoId, int speed, int targetPosition) {
        return false;
    }

    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands) {
        return false;
    }

    public List<NaoServo> getServos() {
        List<NaoServo> servos = new ArrayList<>();
        safeExecuteTask(() -> {
            List<String> jointNames = alMotion.getBodyNames("Body");
            jointNames.forEach(s -> servos.add(new NaoServo(s, alMotion)));
        });

        return servos;
    }

    public boolean setTorgue(String servoId, int limit) {
        return false;
    }

    public boolean setTorgue(String servoId, boolean state) {
        return false;
    }

    public Servo getServo(String servoId) {
        return null;
    }

    public void shutdown() {

    }
}
