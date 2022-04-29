package com.oberasoftware.robo.maximus.motion.cartesian;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.humanoid.cartesian.CartesianControl;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public class CoordinatesMonitor implements Behaviour {
    private static final Logger LOG = getLogger( CoordinatesMonitor.class );

    private static final int CHECK_INTERVAL = 20000;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        LOG.info("Initializing coordinates monitor");
        executorService.submit(() -> {
            LOG.info("Starting coordinates monitor");
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    CartesianControl cartesianControl = behaviouralRobot.getBehaviour(CartesianControl.class);
                    LOG.info("Current Robot Coordinates: {}", cartesianControl.getCurrentCoordinates());

                    sleepUninterruptibly(CHECK_INTERVAL, MILLISECONDS);
                }
            } catch(Exception e) {
                LOG.error("Unexpected error happened on coordinates monitor", e);
            }
            LOG.info("Thread was interrupted");
        });
    }
}
