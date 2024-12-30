package com.oberasoftware.robo.maximus.motion;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.robotics.MotionTask;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.commands.BulkPositionSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.PositionAndSpeedCommand;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.humanoid.MotionEngine;
import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.motion.JointTarget;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.iot.core.robotics.motion.Motion;
import com.oberasoftware.iot.core.robotics.motion.MotionUnit;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import com.oberasoftware.robo.core.motion.JointTargetImpl;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.oberasoftware.robo.maximus.motion.JointControlImpl.RADIAL_SCALE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public class MotionEngineImpl implements MotionEngine, Behaviour {
    private static final Logger LOG = getLogger(MotionEngineImpl.class);

    private static final int FREQUENCY = 50;
//    private static final double FULL_REV_POSITION = 4000;

    private static final Scale TARGET_SCALE = new Scale(0, 4095);

    private static final double REV_MINUTE_UNIT = 0.229;
    private static final double REV_PER_FREQ_INT = (REV_MINUTE_UNIT / 60 / 1000) * FREQUENCY;

    private static final int QUEUE_INTERVAL = 1000;

    private ServoDriver servoDriver;

    private final Queue<MotionTaskImpl> queue = new LinkedBlockingQueue<>();
    private final Map<String, MotionTaskImpl> motionTasks = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile boolean running;

    private final Map<String, Joint> jointMap;

    public MotionEngineImpl(List<Joint> joints) {
        this.jointMap = joints.stream().collect(Collectors.toMap(Joint::getJointId, jv -> jv));
    }

    @Override
    public void initialize(Robot behaviouralRobot, RobotHardware robotCore) {
        this.servoDriver = robotCore.getServoDriver();

        startListener();
    }

    private void startListener() {
        running = true;
        executor.execute(() -> {
            LOG.info("Starting motion queue");
            while(running && !Thread.currentThread().isInterrupted()) {
                MotionTaskImpl task = queue.poll();
                if(task != null && !task.isCancelled()) {
                    try {
                        LOG.info("Starting motion task: {}", task.getTaskId());
                        runMotionLoop(task);
                    } finally {
                        LOG.info("Task: {} is marked as complete", task.getTaskId());
                        task.markComplete();
                        motionTasks.remove(task.getTaskId());
                    }
                } else {
                    sleepUninterruptibly(QUEUE_INTERVAL, MILLISECONDS);
                }
            }
            LOG.info("Motion queue has stopped");
        });
    }

    private int checkAngle(String jointId, int angle) {
        if(jointMap.containsKey(jointId) && jointMap.get(jointId).isInverted()) {
            return -angle;
        } else {
            return angle;
        }
    }

    private void runMotionLoop(MotionTaskImpl task) {
        if(task.getState() == MotionTaskImpl.STATE.NOT_STARTED) {
            task.setState(MotionTask.STATE.STARTED);
            if (task.isLoop()) {
                LOG.info("Starting motion loop for task: {}", task.getTaskId());
                while(task.hasStarted()) {
                    runMotion(task);
                }
                LOG.info("Motion loop for task: {} finished", task.getTaskId());
            } else {
                runMotion(task);
            }
        } else {
            LOG.warn("Cannot start task: {} as has invalid state: {}", task.getTaskId(), task.getState());
        }
    }

    private void runMotion(MotionTaskImpl task) {
        MotionUnit unit = task.getMotion();

        Map<String, Integer> lastServoPositions = new HashMap<>();
        servoDriver.getServos().forEach(s -> {
            int initialPosition = s.getData().getValue(ServoProperty.POSITION);
            Scale scale = s.getData().getValue(ServoProperty.POSITION_SCALE);
            int currentAngle = scale.convertToScale(initialPosition, RADIAL_SCALE);
            LOG.info("Initial position: {} angle: {} for servo: {}", initialPosition, currentAngle, s.getId());

            lastServoPositions.put(s.getId(), currentAngle);
        });
        if(validateAlreadyAtTarget(lastServoPositions, unit)) {
            LOG.info("Motion: {} aborted, joints already at target position", unit);
            return;
        }

        LOG.info("Calculating {} frames for unit: {}", unit.getFrames().size(), unit);
        Stopwatch w = Stopwatch.createStarted();
        var intervalList = unit.getFrames().stream().map(f -> calculateFrame(lastServoPositions, f))
                .flatMap(List::stream)
                .toList();
        LOG.info("Calculated: {} intervals in: {} for motion: {}", intervalList.size(), w.elapsed(MILLISECONDS), unit);

        LOG.info("Enabling torgue");
        task.setState(MotionTask.STATE.RUNNING);
        servoDriver.setTorgueAll(true);

        LOG.info("Requesting lock on thread: {}", Thread.currentThread().getName());
        servoDriver.lock();
        try {
            int counter = 0;
            for (IntervalTarget interval : intervalList) {
                List<JointTarget> targets = interval.getTargets();

                Stopwatch is = Stopwatch.createStarted();
                Map<String, PositionAndSpeedCommand> m = targets.stream()
                        .map(jt -> {
                            String sId = jointMap.get(jt.getJointId()).getServoId();
                            return new PositionAndSpeedCommand(sId, jt.getTargetAngle(), RADIAL_SCALE, jt.getTargetVelocity(), new Scale(0, 100));
                        })
                        .collect(Collectors.toMap(PositionAndSpeedCommand::getServoId, jtm -> jtm));

                servoDriver.bulkSetPositionAndSpeed(m, BulkPositionSpeedCommand.WRITE_MODE.SYNC);
                LOG.info("Completed interval: {} in: {} for frame: {} motorData: {}", counter, is.elapsed(MILLISECONDS), interval.getFrameId(), m);

                counter++;
                sleepUninterruptibly(FREQUENCY, MILLISECONDS);
            }
        } finally {
            servoDriver.unlock();
        }
    }

    private boolean validateAlreadyAtTarget(Map<String, Integer> initialPositions, MotionUnit motion) {
        var lastFrame = motion.getFrames().getLast();
        var jts = lastFrame.getJointTargets();
        Set<String> jtsOffPosition = initialPositions.keySet();
        jts.forEach(jt -> {
            var jointId = jt.getJointId();
            var angle = jt.getTargetAngle();
            String servoId = jointMap.get(jointId).getServoId();

            if(initialPositions.get(servoId) == angle) {
                jtsOffPosition.remove(servoId);
            }
        });

        return jtsOffPosition.isEmpty();
    }

    private List<IntervalTarget> calculateFrame(Map<String, Integer> positions, KeyFrame frame) {
        LOG.debug("Calculate frame: {} with {} joint positions", frame.getKeyFrameId(), frame.getJointTargets().size());

        long timeInMs = frame.getTimeInMs();
        boolean oneShot = timeInMs < FREQUENCY;
        if(oneShot) {
            return Lists.newArrayList(new IntervalTarget(frame.getKeyFrameId(), frame.getJointTargets()));
        } else {
            int iterations = (int)(timeInMs / FREQUENCY);

            var l = IntStream.range(0, iterations).mapToObj(i -> new IntervalTarget(frame.getKeyFrameId(), new ArrayList<>())).collect(Collectors.toList());

            LOG.debug("REv PER FREQ: {}", REV_PER_FREQ_INT);
            for(JointTarget t: frame.getJointTargets()) {
                String servoId = jointMap.get(t.getJointId()).getServoId();
                int currentAngle = positions.get(servoId);

                int targetAngle = checkAngle(t.getJointId(), t.getTargetAngle());
                if(currentAngle != targetAngle) {
                    double deltaPerFrame = (double)(targetAngle - currentAngle) / iterations;

                    double abs = RADIAL_SCALE.getMin() + Math.abs(deltaPerFrame);

                    double deltaTargetPerFrame = RADIAL_SCALE.convertToScale(abs, TARGET_SCALE);

                    LOG.debug("TARGET: {} current: {} delta: {} deltaPerFrameConv: {}", targetAngle, currentAngle, deltaPerFrame, deltaTargetPerFrame);

                    double revAmount = deltaTargetPerFrame / (double)TARGET_SCALE.getMax();
                    int velocityProfile = (int)(revAmount / REV_PER_FREQ_INT);
                    LOG.debug("Rev amount: {} vel prof: {}", revAmount, velocityProfile);

                    for(int i=0; i<iterations; i++) {
                        double interim = (deltaPerFrame * (i + 1));
                        int angle = (int)(currentAngle + interim);

                        LOG.debug("Target: {} at velocity: {} for servo: {}", angle, velocityProfile, t.getJointId());

                        JointTargetImpl jt = new JointTargetImpl(t.getJointId(), 0, angle);
                        jt.setTargetVelocity(velocityProfile);
                        l.get(i).addTarget(jt);
                    }

                    positions.put(servoId, targetAngle);
                } else {
                    LOG.debug("Target angle: {} is already reached for servo: {}", targetAngle, t.getJointId());
                }
            }

            return l;
        }
    }

    private static class IntervalTarget {
        private final List<JointTarget> targets;
        private final String frameId;

        public IntervalTarget(String frameId, List<JointTarget> targets) {
            this.targets = targets;
            this.frameId = frameId;
        }

        public List<JointTarget> getTargets() {
            return targets;
        }

        public String getFrameId() {
            return frameId;
        }

        public void addTarget(JointTarget target) {
            this.targets.add(target);
        }
    }

    @Override
    public MotionTask post(Motion motion) throws RoboException {
        MotionTaskImpl task = new MotionTaskImpl(UUID.randomUUID().toString(), motion);

        if(queue.offer(task)) {
            motionTasks.put(task.getTaskId(), task);
            return task;
        } else {
            throw new RoboException("Could not submit motion task");
        }
    }

    @Override
    public List<MotionTask> getTasks() {
        return null;
    }

    @Override
    public Optional<MotionTask> getTask(String taskId) {
        return Optional.of(motionTasks.get(taskId));
    }

    @Override
    public boolean stop() throws RoboException {
        return false;
    }

    @Override
    public boolean resume() throws RoboException {
        return false;
    }

    @Override
    public void resetTasks() throws RoboException {

    }

    @Override
    public boolean removeTask(String taskId) throws RoboException {
        return motionTasks.computeIfPresent(taskId, (k, v) -> {
            v.setState(MotionTask.STATE.CANCELLED);
            return v;
        }) != null;
    }
}
