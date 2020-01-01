package com.oberasoftware.robo.maximus.motion;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.commands.BulkPositionSpeedCommand;
import com.oberasoftware.robo.api.commands.PositionAndSpeedCommand;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.motion.JointTarget;
import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.motion.MotionUnit;
import com.oberasoftware.robo.api.servo.ServoDriver;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.core.motion.JointTargetImpl;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.oberasoftware.robo.maximus.motion.MotionControlImpl.RADIAL_SCALE;
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

    private Queue<MotionTask> queue = new LinkedBlockingQueue<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile boolean running;

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        this.servoDriver = robotCore.getServoDriver();

        startListener();
    }

    private void startListener() {
        running = true;
        executor.execute(() -> {
            LOG.info("Starting motion queue");
            while(running && !Thread.currentThread().isInterrupted()) {
                MotionTask task = queue.poll();
                if(task != null && !task.isCancelled()) {
                    try {
                        runMotion(task);
                    } finally {
                        task.markComplete();
                    }
                } else {
                    sleepUninterruptibly(QUEUE_INTERVAL, MILLISECONDS);
                }
            }
            LOG.info("Motion queue has stopped");
        });
    }

    private void runMotion(MotionTask task) {
        MotionUnit unit = task.getMotion();

        Map<String, Integer> lastServoPositions = new HashMap<>();
        servoDriver.getServos().forEach(s -> {
            int initialPosition = s.getData().getValue(ServoProperty.POSITION);
            Scale scale = s.getData().getValue(ServoProperty.POSITION_SCALE);
            int currentAngle = scale.convertToScale(initialPosition, RADIAL_SCALE);
            LOG.info("Initial position: {} angle: {} for servo: {}", initialPosition, currentAngle, s.getId());

            lastServoPositions.put(s.getId(), currentAngle);
        });

        LOG.info("Calculating {} frames for unit: {}", unit.getFrames().size(), unit);
        Stopwatch w = Stopwatch.createStarted();
        var intervalList = unit.getFrames().stream().map(f -> calculateFrame(lastServoPositions, f))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        LOG.info("Calculated: {} intervals in: {} for motion: {}", intervalList.size(), w.elapsed(MILLISECONDS), unit);

        LOG.info("Enabling torgue");
        servoDriver.setTorgueAll(true);

        int counter = 0;
        for (IntervalTarget interval : intervalList) {
            List<JointTarget> targets = interval.getTargets();

            Stopwatch is = Stopwatch.createStarted();
            Map<String, PositionAndSpeedCommand> m = targets.stream()
                    .map(jt -> new PositionAndSpeedCommand(jt.getServoId(), jt.getTargetAngle(), RADIAL_SCALE, jt.getTargetVelocity(), new Scale(0, 100)))
                    .collect(Collectors.toMap(PositionAndSpeedCommand::getServoId, jtm -> jtm));

            servoDriver.bulkSetPositionAndSpeed(m, BulkPositionSpeedCommand.WRITE_MODE.SYNC);
            LOG.info("Completed interval: {} in: {} for frame: {} motorData: {}", counter, is.elapsed(MILLISECONDS), interval.getFrameId(), m);

            counter++;
            sleepUninterruptibly(FREQUENCY, MILLISECONDS);
        }
    }

    private List<IntervalTarget> calculateFrame(Map<String, Integer> positions, KeyFrame frame) {
        LOG.info("Calculate frame: {} with {} joint positions", frame.getKeyFrameId(), frame.getJointTargets().size());

        long timeInMs = frame.getTimeInMs();
        boolean oneShot = timeInMs < FREQUENCY;
        if(oneShot) {
            return Lists.newArrayList(new IntervalTarget(frame.getKeyFrameId(), frame.getJointTargets()));
        } else {
            int iterations = (int)(timeInMs / FREQUENCY);

            var l = IntStream.range(0, iterations).mapToObj(i -> new IntervalTarget(frame.getKeyFrameId(), new ArrayList<>())).collect(Collectors.toList());

            LOG.info("REv PER FREQ: {}", REV_PER_FREQ_INT);


            for(JointTarget t: frame.getJointTargets()) {
                int currentAngle = positions.get(t.getServoId());

                int targetAngle = t.getTargetAngle();
                if(currentAngle != targetAngle) {
                    double deltaPerFrame = (double)(targetAngle - currentAngle) / iterations;

                    double abs = RADIAL_SCALE.getMin() + Math.abs(deltaPerFrame);

                    double deltaTargetPerFrame = RADIAL_SCALE.convertToScale(abs, TARGET_SCALE);

                    LOG.info("TARGET: {} current: {} delta: {} deltaPerFrameConv: {}", targetAngle, currentAngle, deltaPerFrame, deltaTargetPerFrame);

                    double revAmount = deltaTargetPerFrame / (double)TARGET_SCALE.getMax();
                    int velocityProfile = (int)(revAmount / REV_PER_FREQ_INT);
                    LOG.info("Rev amount: {} vel prof: {}", revAmount, velocityProfile);

                    for(int i=0; i<iterations; i++) {
                        double interim = (deltaPerFrame * (i + 1));
                        int angle = (int)(currentAngle + interim);

                        LOG.info("Target: {} at velocity: {} for servo: {}", angle, velocityProfile, t.getServoId());

                        JointTargetImpl jt = new JointTargetImpl(t.getServoId(), 0, angle);
                        jt.setTargetVelocity(velocityProfile);
                        l.get(i).addTarget(jt);
                    }

                    positions.put(t.getServoId(), targetAngle);
                } else {
                    LOG.info("Target angle: {} is already reached for servo: {}", targetAngle, t.getServoId());
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
        MotionTask task = new MotionTask(UUID.randomUUID().toString(), motion);

        if(queue.offer(task)) {
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
        return Optional.empty();
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
        return false;
    }
}
