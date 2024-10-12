package com.oberasoftware.robo.maximus.motion.cartesian;

import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import com.oberasoftware.iot.core.robotics.humanoid.JointControl;
import com.oberasoftware.iot.core.robotics.humanoid.MotionEngine;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CartesianControl;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CartesianMoveInput;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.Coordinates;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CoordinatesImpl;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointData;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.oberasoftware.iot.core.robotics.humanoid.components.ComponentNames.*;
import static com.oberasoftware.robo.maximus.motion.cartesian.CartesianUtil.*;
import static java.lang.Math.abs;
import static org.slf4j.LoggerFactory.getLogger;

public class CartesianControlImpl implements CartesianControl {
    private static final Logger LOG = getLogger( CartesianControlImpl.class );

    //all lengths are in millimeters (mm)
    private static final int THIGH_LENGTH = 158;
    private static final int SHIN_LENGTH = 145;

    //calculation statistics
    private static final double DEGREES_90 = 90.0;
    private static final int DEGREES_180 = 180;

    private static final int MAX_FRAME_TIME_MS = 400;
    private static final int MAGIC_NUMBER_LEAN_CORRECTION = 2;
    private static final int MAGIC_NUMBER_ANKLE_CORRECTION = 2;

    private MotionEngine motionEngine;
    private JointControl motionControl;

    private Map<String, Joint> jointMap;
    private final Map<String, List<Joint>> jointTypeMap = new HashMap<>();

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
        this.motionEngine = behaviouralRobot.getBehaviour(MotionEngine.class);
        this.motionControl = behaviouralRobot.getBehaviour(JointControl.class);

        JointBasedRobot r = (JointBasedRobot) behaviouralRobot;
        jointMap = r.getJoints(true).stream().collect(Collectors.toMap(Joint::getName, k -> k));

        r.getJoints(true).forEach(j -> {
            var jt = j.getJointType();
            jointTypeMap.putIfAbsent(jt, new ArrayList<>());
            jointTypeMap.get(jt).add(j);
        });
    }

    @Override
    public boolean move(CartesianMoveInput input) {
        LOG.info("Cartesian input entity move: {}", input);
        if(input.getMode() == CartesianMoveInput.MOVE_MODE.ABSOLUTE) {
            setCoordinates(EFFECTED_PART.TORSO, input.getX(), input.getY(), input.getZ(), input.getTime(), input.getUnit());
        } else {
            move(EFFECTED_PART.TORSO, input.getX(), input.getY(), input.getZ(), input.getTime(), input.getUnit());
        }

        return false;
    }

    @Override
    public boolean setCoordinates(EFFECTED_PART part, double x, double y, double z, long time, TimeUnit unit) {
        LOG.info("Setting absolute coordinates x: {} y: {} z: {}", x, y, z);

        Coordinates currentCoordinates = getCurrentCoordinates();
        LOG.info("Current coordinates: {} of part: {}", currentCoordinates, part);

        double xDelta = x - currentCoordinates.getX();
        double yDelta = y - currentCoordinates.getY();
        double zDelta = z - currentCoordinates.getZ();

        return move(part, xDelta, yDelta, zDelta, time, unit);
    }

    @Override
    public boolean move(EFFECTED_PART part, double xDelta, double yDelta, double zDelta, long time, TimeUnit unit) {
        LOG.info("Cartesian delta move of: {} x: {} y: {} z: {} in: {} ms.", part, xDelta, yDelta, zDelta, unit.toMillis(time));
        long timeInMs = unit.toMillis(time);
        int frames = (int)timeInMs / MAX_FRAME_TIME_MS;

        Coordinates currentCoordinates = getCurrentCoordinates();
        LOG.info("Current coordinates: {} of part: {}", currentCoordinates, part);

        double xStep = xDelta / frames;
        double yStep = yDelta / frames;
        double zStep = zDelta / frames;

        List<LegAngles> angles = new ArrayList<>();
        for(int frameNr = 0; frameNr <= frames; frameNr++) {
            var x = (xStep * frameNr) + currentCoordinates.getX();
            var y = (yStep * frameNr) + currentCoordinates.getY();
            var z = (zStep * frameNr) + currentCoordinates.getZ();

            Optional<LegAngles> legAngles = translate(part, x, y, z);
            legAngles.ifPresent(angles::add);
        }
        Motion motion = translateToMotion(angles, timeInMs);
        LOG.info("Posting motion: {}", motion);
        return motionEngine.post(motion) != null;
//        return true;
    }

    @Override
    public Coordinates getCurrentCoordinates() {
        LegAngles currentAngles = getCurrentAngles();
        LOG.info("Current angles: {}", currentAngles);
        return calculateCurrentZY(currentAngles.getAngle(HIP_PITCH),
                currentAngles.getAngle(KNEE));
    }

    public Coordinates calculateCurrentZY(double hipPitch, double knee) {
        var kneePitchCorrection = DEGREES_180 - abs(knee);

        var zyLength = lawOfCosinesWithAngle(THIGH_LENGTH, SHIN_LENGTH, kneePitchCorrection);
        var zThighAngle = lawOfSinesTwoSides(SHIN_LENGTH, zyLength, kneePitchCorrection);

        var zZYAngle = abs(hipPitch) - zThighAngle;
        var restAngle = DEGREES_180 - zZYAngle - DEGREES_90;
        var y = lawOfSinesTwoAngles(zyLength, zZYAngle, DEGREES_90);
        var z = lawOfSinesTwoAngles(zyLength, restAngle, DEGREES_90);

        LOG.info("Current Z height: {} and Y offset: {}", z, y);
        return new CoordinatesImpl(0, y, z);
    }


    private LegAngles getCurrentAngles() {
        LegAngles angles = new LegAngles();
        jointMap.forEach((jk, j) -> {
            var jointType = j.getJointType();
            Optional<JointData> jd = motionControl.getJointData(j.getJointId());
            if(jd.isPresent()) {
                Double degrees = (double) jd.get().getDegrees();
                if (angles.contains(jointType)) {
                    var drift = abs(angles.getDrift(jointType, degrees));
                    if (drift > 0.02) {
                        LOG.warn("Joint Type: {} with joint: {} is drifting: {} % from its peer", jointType, jk, (drift * 100));
                    }
                } else {
                    angles.addAngleByType(jointType, degrees);
                }
            }
        });

        return angles;
    }

    private Motion translateToMotion(List<LegAngles> angles, long totalTime) {
        int size = angles.size();
        if(size > 0) {
            Map<String, Double> maxDeltas = calculateDelta(angles.get(0), angles.get(size - 1));
            LOG.info("We got the following deltas: {}", maxDeltas);

            List<KeyFrame> keyFrames = new ArrayList<>();
            for(int i=0; i<angles.size(); i++) {
                LegAngles currentFrame = angles.get(i);
                LegAngles previousFrame = i > 0 ? angles.get(i - 1) : null;

                long frameTime = calculateFrameTime(maxDeltas, totalTime, currentFrame, previousFrame);
                KeyFrame keyFrame = currentFrame.toKeyFrame(jointTypeMap, frameTime);
                LOG.info("Created keyframe: {} timeInMs: {}", keyFrame.getKeyFrameId(), keyFrame.getTimeInMs());
                keyFrames.add(keyFrame);
            }

//            List<KeyFrame> keyFrames = angles.stream().map(a -> a.toKeyFrame(jointTypeMap, MAX_FRAME_TIME_MS)).collect(Collectors.toList());
            LOG.info("Collected key frames: {}", keyFrames);
            return new MotionImpl(UUID.randomUUID().toString(), keyFrames);
        } else {
            throw new RoboException("No Leg Angles to translate");
        }
    }

    private long calculateFrameTime(Map<String, Double> maxDeltas, long totalTime, LegAngles currentFrame, LegAngles previousFrame) {
        if(previousFrame != null) {
            Map<String, Double> deltas = calculateDelta(currentFrame, previousFrame);

            double totalPercentage = 0.0;
            int counter = 0;
            for (Map.Entry<String, Double> e : deltas.entrySet()) {
                double maxDelta = maxDeltas.get(e.getKey());
                double currentDelta = deltas.get(e.getKey());
                double deltaPct = currentDelta / maxDelta;

                if(!Double.isNaN(deltaPct)) {
                    totalPercentage += deltaPct;
                    counter++;
                }

                LOG.debug("Frame delta %: {} for joint type: {}", deltaPct, e.getKey());
            }
            long frametime = (long)((totalPercentage / counter) * totalTime);
            return frametime < MAX_FRAME_TIME_MS ? MAX_FRAME_TIME_MS : frametime;
        }
        LOG.debug("No previous frame available, frametime is 0");
        return MAX_FRAME_TIME_MS;
    }

    private Map<String, Double> calculateDelta(LegAngles angles1, LegAngles angles2) {
        Map<String, Double> deltas = new HashMap<>();
        angles1.getJointTypes().forEach(jt -> {
            deltas.put(jt, abs(angles1.getAngle(jt) - angles2.getAngle(jt)));
        });

        return deltas;
    }

    private Optional<LegAngles> translate(EFFECTED_PART part, double x, double y, double z) {
        switch (part) {
            case TORSO:
                return translateTorso(x, y, z);
            case WHOLE_BODY:
                throw new RoboException("Whole Body translation is not yet implemented");
            default:
                throw new IllegalStateException("Unexpected value: " + part);
        }
    }

    public Optional<LegAngles> translateTorso(double x, double y, double z) {
        LOG.info("Translating Torso x: {} y: {}, z: {}", x, y, z);

        double zy = lawOfCosinesWithAngle(y, z, DEGREES_90);
        double zyAngle = lawOfSinesTwoSides(y, zy, DEGREES_90);

        double anklePitch = lawOfCosines(SHIN_LENGTH, THIGH_LENGTH, zy) + MAGIC_NUMBER_ANKLE_CORRECTION;
        double kneePitch = lawOfCosines(SHIN_LENGTH, zy, THIGH_LENGTH);

        double hipPitch = DEGREES_180 - kneePitch - anklePitch;
        double hipPitchCorrection = (abs(hipPitch + abs(zyAngle))) + MAGIC_NUMBER_LEAN_CORRECTION;

        //knee fully stretched is 180 degrees == 0 degrees on dynamixel (range is therefore -90 to +90), 3d model has a fault which causes a +8 offset fix
        kneePitch = DEGREES_180 - kneePitch;

        LOG.info("Angles for Ankle: {} Knee: {} Hip: {}", anklePitch, kneePitch, hipPitchCorrection);

        boolean invalid = Double.isNaN(anklePitch) && Double.isNaN(kneePitch) && Double.isNaN(hipPitchCorrection);
        if(invalid) {
            return Optional.empty();
        } else {
            LegAngles angles = new LegAngles();
            angles.addAngleByType(ANKLE_PITCH, anklePitch);
            angles.addAngleByType(KNEE, kneePitch);
            angles.addAngleByType(HIP_PITCH, hipPitchCorrection);
            angles.addAngleByType(HIP_ROLL, 0.0);
            angles.addAngleByType(ANKLE_ROLL, 0.0);

            return Optional.of(angles);
        }
    }
}
