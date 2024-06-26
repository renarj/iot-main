package com.oberasoftware.robo.cloud;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.events.ThingValueEvent;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.SpeechEngine;
import com.oberasoftware.robo.cloud.motion.RemoteMotionEngine;
import com.oberasoftware.robo.core.CoreConfiguration;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Renze de Vries
 */
@SpringBootApplication
@Import({
        RemoteConfiguration.class,
        CoreConfiguration.class
})
public class RemoteRobotTest {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteRobotTest.class);

    public static void main(String[] args) {
        LOG.info("Starting hybrid robot test");

        ApplicationContext context = new SpringApplication(RemoteRobotTest.class).run(args);

        RobotHardware max = new HardwareRobotBuilder("max", context)
                .motionEngine(RemoteMotionEngine.class)
                .remote(RemoteCloudDriver.class, true)
                .build();

        RobotHardware pep = new HardwareRobotBuilder("peppy", context)
                .motionEngine(RemoteMotionEngine.class)
                .capability(RemoteSpeechEngine.class)
                .remote(RemoteCloudDriver.class, true)
                .build();

        MaxRobotEventHandler maxHandler = new MaxRobotEventHandler(pep);
        max.listen(maxHandler);

        PepRobotEventHandler pepHandler = new PepRobotEventHandler(max);
        pep.listen(pepHandler);

        max.getMotionEngine().prepareWalk();
        pep.getMotionEngine().prepareWalk();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            max.shutdown();
        }));
    }

    public static class MaxRobotEventHandler implements EventHandler {
        private RobotHardware pep;

        private AtomicBoolean guard = new AtomicBoolean(true);

        private MaxRobotEventHandler(RobotHardware pep) {
            this.pep = pep;
        }

        @EventSubscribe
        public void receive(ThingValueEvent valueEvent) {
            LOG.info("Received a distance: {}", valueEvent.getValue().asString());
            if(valueEvent.getControllerId().equals("max") && valueEvent.getAttribute().equals("distance")) {
                int distance = valueEvent.getValue().getValue();
                if(distance < 30) {
                    if(guard.compareAndSet(true, false)) {
                        LOG.info("Distance is too small: {}", distance);
                        pep.getCapability(SpeechEngine.class).say("Max, are you ok, did you hit something?", "english");

                        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
                        guard.set(true);
                        LOG.info("Allowing further distance events");
                    }
                }
            }
        }
    }

    public static class PepRobotEventHandler implements EventHandler {
        private RobotHardware max;

        public PepRobotEventHandler(RobotHardware max) {
            this.max = max;
        }

        @EventSubscribe
        public void receive(ThingValueEvent valueEvent) {
            LOG.info("Received an event for pep: {}", valueEvent);
            if(valueEvent.getControllerId().equals("peppy") && valueEvent.getThingId().equals("head")) {
                if(valueEvent.getValue().asString().equals("true")) {

                    max.getMotionEngine().runMotion("Bravo");
                }
            }
        }
    }
}
