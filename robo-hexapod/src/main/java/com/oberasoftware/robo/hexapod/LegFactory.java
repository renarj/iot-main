package com.oberasoftware.robo.hexapod;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.RobotRegistry;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.core.commands.OperationModeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

@Component
public class LegFactory {
    private static final Logger LOG = LoggerFactory.getLogger(LegFactory.class);

    public static HexRobotBaseData BASE_DATA = new HexRobotBaseData(4.8, 6.71, 13.5,
            31, 15, 20);


    private static final BiFunction<Double, Double, Double> xFunction = (coxaLength, femurLength) -> Math.cos(60.0/180.0 * Math.PI) *  (coxaLength + femurLength);
    private static final BiFunction<Double, Double, Double> xNegativeFunction = (coxaLength, femurLength) -> -(Math.cos(60.0/180.0 * Math.PI) * (coxaLength + femurLength));
    private static final BiFunction<Double, Double, Double> yFunction = (coxaLength, femurLength) -> Math.sin(60.0/180.0 * Math.PI) * (coxaLength + femurLength);
    private static final BiFunction<Double, Double, Double> yNegativeFunction = (coxaLength, femurLength) -> Math.sin(-60.0/180.0 * Math.PI) * (coxaLength + femurLength);

    private Lock lock = new ReentrantLock();

    private List<Leg> legs = new CopyOnWriteArrayList<>();

    @Autowired
    private RobotRegistry robotRegistry;

    public List<Leg> getLegs() {
        ensureLegsInitialised();

        return Lists.newArrayList(legs);
    }

    public Leg getLeg(String legName) {
        ensureLegsInitialised();

        return legs.stream().filter(l -> l.getLegId().equalsIgnoreCase(legName))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not find leg with name: " + legName));
    }

    private void ensureLegsInitialised() {
        if(legs.isEmpty()) {
            lock.lock();
            try {
                initLegs();
            } finally {
                lock.unlock();
            }
        }
    }

    private void initLegs() {
        Robot robot = robotRegistry.getRobots().get(0);

        ServoDriver servoDriver = robot.getServoDriver();
        servoDriver.getServos().forEach(s -> {
            servoDriver.sendCommand(new OperationModeCommand(s.getId(), OperationModeCommand.MODE.POSITION_CONTROL));
        });
        servoDriver.getServos().forEach(s -> s.setSpeed(50, new Scale(-100, 100)));
        servoDriver.getServos().forEach(Servo::enableTorgue);


        PosData leg1PosData = createInitialFeetPosition(BASE_DATA, xFunction, yFunction);
        PosData leg2PosData = createInitialFeetPosition(BASE_DATA,
                (coxaLength, femurLength) -> coxaLength + femurLength,
                (coxaLength, femurLength) -> 0.0);
        PosData leg3PosData = createInitialFeetPosition(BASE_DATA,
                xFunction, yNegativeFunction);
        PosData leg4PosData = createInitialFeetPosition(BASE_DATA,
                xNegativeFunction, yNegativeFunction);
        PosData leg5PosData = createInitialFeetPosition(BASE_DATA,
                (coxaLength, femurLength) -> -(coxaLength + femurLength),
                (coxaLength, femurLength) -> 0.0);
        PosData leg6PosData = createInitialFeetPosition(BASE_DATA,
                xNegativeFunction, yFunction);

        Leg leg1 = new Leg("TopRight", 7.5, 15.5, leg1PosData, servoDriver.getServo("8"), servoDriver.getServo("10"), servoDriver.getServo("3"), -60.0, 1); //is ok
        Leg leg2 = new Leg("MiddleRight", 10, 0, leg2PosData, servoDriver.getServo("15"), servoDriver.getServo("5"), servoDriver.getServo("16"), 0.0, -1);
        Leg leg3 = new Leg("BottomRight", 7.5, -15.5, leg3PosData, servoDriver.getServo("18"), servoDriver.getServo("14"), servoDriver.getServo("1"), 60.0, -1);
        Leg leg4 = new Leg("BottomLeft", -7.5,-15.5, leg4PosData, servoDriver.getServo("7"), servoDriver.getServo("2"), servoDriver.getServo("4"), -240.0, 1); // is ok
        Leg leg5 = new Leg("MiddleLeft", -10, 0, leg5PosData, servoDriver.getServo("12"), servoDriver.getServo("6"), servoDriver.getServo("9"), -180.0, -1);
        Leg leg6 = new Leg("TopLeft", -7.5, 15.5, leg6PosData, servoDriver.getServo("11"), servoDriver.getServo("13"), servoDriver.getServo("17"), -120.0, -1);

        legs = Lists.newArrayList(leg1, leg2, leg3, leg4, leg5, leg6);
    }

    private static PosData createInitialFeetPosition(HexRobotBaseData baseData, BiFunction<Double, Double, Double> xFunction, BiFunction<Double, Double, Double> yFunction) {
        double x = xFunction.apply(baseData.getCoxaLength(), baseData.getFemurLength());
        double y = yFunction.apply(baseData.getCoxaLength(), baseData.getFemurLength());

        return new PosData(x, y, baseData.getTibiaLength());
    }

}
