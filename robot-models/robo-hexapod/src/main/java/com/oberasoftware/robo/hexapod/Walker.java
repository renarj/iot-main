package com.oberasoftware.robo.hexapod;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.servo.Servo;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Walker {
    private static final Logger LOG = LoggerFactory.getLogger(Walker.class);

    private static final double DYNA_DEGREE_CONVERSION = 512.0/150.0;

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private LegFactory legFactory;

    public void stop() {
        robotRegistry.getRobots().get(0).getServoDriver().getServos().forEach(Servo::disableTorgue);
    }

    public RobotFeedback walk(PosData dirInput, PosData rotInput) {

        List<Leg> legs = legFactory.getLegs();

        legs.forEach(l -> LOG.info("Leg: {} Data: {}", l.getLegId(), l));

        Map<String, PositionAndSpeedCommand> m = new HashMap<>();
        Map<String, AngleData> legAngleMap = new HashMap<>();
        legs.forEach(l -> {
            PosData bodyIKLeg = createBodyIKLeg(l, dirInput, rotInput);
            AngleData angles = createIKLeg(l, LegFactory.BASE_DATA, bodyIKLeg, dirInput);
            LOG.info("Leg: {} angles: {}", l.getLegId(), angles);

            legAngleMap.put(l.getLegId(), angles);

            m.put(l.getCoxia().getId(), new PositionAndSpeedCommand(l.getCoxia().getId(), (int)angles.getCoxaAngle(), new Scale(0, 1023), 200, new Scale(0, 1023)));
            m.put(l.getFemur().getId(), new PositionAndSpeedCommand(l.getFemur().getId(), (int)angles.getFemurAngle(), new Scale(0, 1023), 200, new Scale(0, 1023)));
            m.put(l.getTibia().getId(), new PositionAndSpeedCommand(l.getTibia().getId(), (int)angles.getTibiaAngle(),new Scale(0, 1023), 200, new Scale(0, 1023)));
        });

        RobotHardware robot = robotRegistry.getRobots().get(0);
        ServoDriver servoDriver = robot.getServoDriver();
        servoDriver.bulkSetPositionAndSpeed(m);

        return new RobotFeedback(robot.getName(), dirInput, rotInput, legAngleMap);
    }

    public RobotFeedback setLeg(String legName, PosData dirInput, PosData rotInput) {
        RobotHardware robot = robotRegistry.getRobots().get(0);
        ServoDriver servoDriver = robot.getServoDriver();

        Leg leg = legFactory.getLeg(legName);

        AngleData legAngles = createLegAngles(leg, dirInput, rotInput);
        servoDriver.bulkSetPositionAndSpeed(createCommand(leg, legAngles));

        Map<String, AngleData> legAnglesMap = new HashMap<>();
        legAnglesMap.put(legName, legAngles);

        return new RobotFeedback(robot.getName(), dirInput, rotInput, legAnglesMap);
    }

    public void loopLeg() {
        RobotHardware robot = robotRegistry.getRobots().get(0);
        ServoDriver servoDriver = robot.getServoDriver();


        Leg frontRight = legFactory.getLeg("TopRight");
        Leg middleLeft = legFactory.getLeg("MiddleLeft");
        Leg bottomRight = legFactory.getLeg("BottomRight");

        List<PosData> positions = Lists.newArrayList(
                new PosData(0, 5, -6),
                new PosData(0, 5, 0),
                new PosData(0, -5, 0),
                new PosData(0, -5, -6));

        for(int i=0; i<10; i++) {
            for (PosData position : positions) {
                AngleData fRightAngles = createLegAngles(frontRight, position, new PosData(0, 0, 0));
                AngleData mLeftAngles = createLegAngles(middleLeft, position, new PosData(0, 0, 0));
                AngleData bRightAngles = createLegAngles(bottomRight, position, new PosData(0, 0, 0));

                Map<String, PositionAndSpeedCommand> fRightCommand = createCommand(frontRight, fRightAngles);
                Map<String, PositionAndSpeedCommand> mLeftCommand = createCommand(middleLeft, mLeftAngles);
                Map<String, PositionAndSpeedCommand> bRightCommand = createCommand(bottomRight, bRightAngles);

                LOG.info("Angle for front right: {} position: {}", fRightAngles, position);
                LOG.info("Angle for middle left: {} position: {}", mLeftAngles, position);
                LOG.info("Angle for bottom right: {} position: {}", bRightAngles, position);

                Map<String, PositionAndSpeedCommand> combinedMap = new HashMap<>();
                combinedMap.putAll(fRightCommand);
                combinedMap.putAll(mLeftCommand);
                combinedMap.putAll(bRightCommand);

                LOG.info("End of position : {}", position);

                servoDriver.bulkSetPositionAndSpeed(combinedMap);
                Uninterruptibles.sleepUninterruptibly(700, TimeUnit.MILLISECONDS);
            }

            LOG.info("End of iteration : {}", i);
        }

        LOG.info("Completed loop");
    }

    public void walkDirection() {
        Leg frontRight = legFactory.getLeg("TopRight");
        Leg middleLeft = legFactory.getLeg("MiddleLeft");
        Leg bottomRight = legFactory.getLeg("BottomRight");
        List<Leg> group1 = Lists.newArrayList(frontRight, middleLeft, bottomRight);

        Leg frontLeft = legFactory.getLeg("TopLeft");
        Leg middleRight = legFactory.getLeg("MiddleRight");
        Leg bottomLeft = legFactory.getLeg("BottomLeft");
        List<Leg> group2 = Lists.newArrayList(frontLeft, middleRight, bottomLeft);

        LOG.info("Setting to initial position");
        walk(new PosData(0, 0,0), new PosData(0, 0, 0));

        List<Frame> frames = Lists.newArrayList(
                new Frame(group1, new PosData(0, 0, -3)),
                new Frame(group2, new PosData(0, 5, 2)),
                new Frame(group1, new PosData(0, -5, -3)),
                new Frame(group1, new PosData(0, -5, 2)),
                new Frame(group2, new PosData(0, 0, -3)),
                new Frame(group1, new PosData(0, 5, 2)),
                new Frame(group2, new PosData(0, -5, -3)),
                new Frame(group2, new PosData(0, -5, 2))
        );


        RobotHardware robot = robotRegistry.getRobots().get(0);
        ServoDriver servoDriver = robot.getServoDriver();

        LOG.info("Starting walk");
        for(int i=0; i<5; i++) {
            for(Frame frame: frames) {
                servoDriver.bulkSetPositionAndSpeed(createLegGroupCommands(frame.getPosData(), frame.getLegs()));
                LOG.info("Executing frame: {}", frame);

                Uninterruptibles.sleepUninterruptibly(800, TimeUnit.MILLISECONDS);
            }

            LOG.info("Finished walk loop: {}", i);
        }
        LOG.info("Finished walking loop");

        LOG.info("Setting to initial position");
        walk(new PosData(0, 0,0), new PosData(0, 0, 0));
    }

    public Map<String, PositionAndSpeedCommand> createLegGroupCommands(PosData position, List<Leg> legs) {
        RobotHardware robot = robotRegistry.getRobots().get(0);
        ServoDriver servoDriver = robot.getServoDriver();


//        Leg frontRight = legFactory.getLeg("TopRight");
//        Leg middleLeft = legFactory.getLeg("MiddleLeft");
//        Leg bottomRight = legFactory.getLeg("BottomRight");

//        List<PosData> positions = Lists.newArrayList(
//                new PosData(0, 5, -6),
//                new PosData(0, 5, 0),
//                new PosData(0, -5, 0),
//                new PosData(0, -5, -6));

//        for(int i=0; i<10; i++) {
//            for (PosData position : positions) {
        Map<String, PositionAndSpeedCommand> combinedMap = new HashMap<>();
        for(Leg leg : legs) {
            AngleData legAngles = createLegAngles(leg, position, new PosData(0, 0, 0));
            LOG.info("Angle for leg: {} position: {}", leg, position);

            Map<String, PositionAndSpeedCommand> legCommands = createCommand(leg, legAngles);

            combinedMap.putAll(legCommands);

        }

        return combinedMap;
    }

    private Map<String, PositionAndSpeedCommand> createCommand(Leg l, AngleData angles) {
        Map<String, PositionAndSpeedCommand> m = new HashMap<>();

        if(angles.getCoxaAngle() != Double.NaN) {
            m.put(l.getCoxia().getId(), new PositionAndSpeedCommand(l.getCoxia().getId(), (int) angles.getCoxaAngle(), new Scale(0, 1023),200, new Scale(0, 1023)));
        } else {
            LOG.warn("Invalid angle for tibia on leg: {}", l, angles);
        }

        if(angles.getFemurAngle() != Double.NaN) {
            m.put(l.getFemur().getId(), new PositionAndSpeedCommand(l.getFemur().getId(), (int) angles.getFemurAngle(), new Scale(0, 1023),200, new Scale(0, 1023)));
        } else {
            LOG.warn("Invalid angle for tibia on leg: {}", l, angles);
        }

        if(angles.getTibiaAngle() != Double.NaN) {
            m.put(l.getTibia().getId(), new PositionAndSpeedCommand(l.getTibia().getId(), (int) angles.getTibiaAngle(),new Scale(0, 1023), 200, new Scale(0, 1023)));
        } else {
            LOG.warn("Invalid angle for tibia on leg: {}", l, angles);
        }

        return m;
    }

    private AngleData createLegAngles(Leg leg, PosData dirInput, PosData rotInput) {
        PosData d = createBodyIKLeg(leg, dirInput, rotInput);
        AngleData a = createIKLeg(leg, LegFactory.BASE_DATA, d, dirInput);

        return a;
    }

    private static PosData createBodyIKLeg(Leg leg, PosData xyzInput, PosData rotInput) {
        double totalY = leg.getInitialPosition().getY() + leg.getOffsetY() + xyzInput.getY();
        double totalX = leg.getInitialPosition().getX() + leg.getOffsetX() + xyzInput.getX();
        double distBodyCenterFeet = Math.sqrt(Math.pow(totalY, 2) + Math.pow(totalX, 2));
        double angleBodyCenterX = (Math.PI / 2.0) - Math.atan2(totalX, totalY);

        double rollZ = Math.tan(rotInput.getZ() * Math.PI / 180.0) * totalX;
        double pitchZ = Math.tan(rotInput.getX() * Math.PI / 180.0) * totalY;

        double IKX = Math.cos(angleBodyCenterX + (rotInput.getY() * Math.PI / 180.0)) * distBodyCenterFeet - totalX;
        double IKY = (Math.sin(angleBodyCenterX + (rotInput.getY() * Math.PI / 180.0)) * distBodyCenterFeet) - totalY;
        double IKZ = rollZ + pitchZ;

        LOG.info("Calc Body IK Leg: {} totalY: {} totalX: {} disBodyCenter: {} angleX: {} rollZ: {} pitchZ: {} IKX: {} IKY: {} IKZ: {}", leg.getLegId(),
                totalY, totalX, distBodyCenterFeet, angleBodyCenterX, rollZ, pitchZ, IKX, IKY, IKZ);

        return new PosData(IKX, IKY, IKZ);
    }

    private static AngleData createIKLeg(Leg leg, HexRobotBaseData baseData, PosData bodyIK, PosData dirInput) {
        double cLength = baseData.getCoxaLength();
        double fLength = baseData.getFemurLength();
        double tLength = baseData.getTibiaLength();


        double newPosX = leg.getInitialPosition().getX() + dirInput.getX() + bodyIK.getX();
        double newPosZ = leg.getInitialPosition().getZ() + dirInput.getZ() + bodyIK.getZ();
        double newPosY = leg.getInitialPosition().getY() + dirInput.getY() + bodyIK.getY();

        LOG.info("Leg: {} POSX: {} POSZ: {} POSY: {}", leg.getLegId(), newPosX, newPosZ, newPosY);
//
        double coxaFeetDist = Math.sqrt(powOf2(newPosX) + powOf2(newPosY));
        double iksw = Math.sqrt(powOf2((coxaFeetDist - cLength)) + powOf2(newPosZ));

        double ika1 = Math.atan((coxaFeetDist - cLength) / newPosZ);
        double ika2 = Math.acos((powOf2(tLength) - powOf2(fLength) - powOf2(iksw)) / (-2 * iksw * fLength));
        double tangle = Math.acos((powOf2(iksw) - powOf2(tLength) - powOf2(fLength)) / (-2 * fLength * tLength));

        LOG.info("Leg: {} feetDist: {} iksw: {} ika1: {} ika2: {} tangle: {}", leg.getLegId(), coxaFeetDist, iksw, ika1, ika2, tangle);
//
        double tibiaAngle = 90.0 - tangle * 180.0 / Math.PI;
        double femurAngle = 90.0 - (ika1 + ika2) * 180.0/Math.PI;
        double coxaAngle = 90.0 - Math.atan2(newPosX, newPosY) * 180.0/Math.PI;

        LOG.info("Leg: {} Tibia Angle: {} Femur Angle: {} Coxa Angle: {}", leg.getLegId(), tibiaAngle, femurAngle, coxaAngle);
//
//
        return new AngleData(convertToDynamixelPosition(coxaAngle  + leg.getCoxaOffset(), -1),
                convertToDynamixelPosition(femurAngle - 30, leg.getDirMod()),
                    convertToDynamixelPosition(tibiaAngle - 60, -leg.getDirMod()));
    }

    private static double convertToDynamixelPosition(double angle, double mod) {
        double r = (mod * (angle * DYNA_DEGREE_CONVERSION)) + 512;
//        LOG.debug("Convert: {} with mod: {} to value: {} rsult: {}", angle, mod, (angle * DYNA_DEGREE_CONVERSION), r);
        return r;
    }

    private static Double powOf2(Double v) {
        return Math.pow(v, 2);
    }
}
