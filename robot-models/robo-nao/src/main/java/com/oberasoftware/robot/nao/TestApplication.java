package com.oberasoftware.robot.nao;

import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.CoreConfiguation;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.SpeechEngine;
import com.oberasoftware.iot.core.robotics.events.BumperEvent;
import com.oberasoftware.iot.core.robotics.events.DistanceSensorEvent;
import com.oberasoftware.iot.core.robotics.events.TextualSensorEvent;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.BumperSensor;
import com.oberasoftware.robot.nao.sensors.NaoSensorDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        NaoConfiguration.class, CoreConfiguation.class
})
@ComponentScan
public class TestApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TestApplication.class);

    public static void main(String[] args) {
        LOG.info("Starting Robot Service Application container");

        SpringApplication springApplication = new SpringApplication(TestApplication.class);
        ConfigurableApplicationContext context = springApplication.run(args);
        NaoSessionManager sessionManager = context.getBean(NaoSessionManager.class);
        NaoUtil.safeExecuteTask(() -> new ALAutonomousLife(sessionManager.getSession()).setState("disabled"));


        RobotHardware robot = new HardwareRobotBuilder("peppy", context)
//                .motionEngine(NaoMotionEngine.class)
//                .servoDriver(NaoServoDriver.class)
                .capability(NaoSpeechEngine.class)
                .capability(NaoQRScanner.class)
//                .sensor(new DistanceSensor("distance", NaoSensorDriver.SONAR_PORT), NaoSensorDriver.class)
                .sensor(new BumperSensor("head", NaoSensorDriver.TOUCH_HEAD), NaoSensorDriver.class)
                .build();
        LOG.info("Robot has been constructed");

        RobotEventHandler eventHandler = new RobotEventHandler(robot);
        robot.listen(eventHandler);

//        robot.getMotionEngine().prepareWalk();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));

    }

    public static class RobotEventHandler implements EventHandler {
        private RobotHardware robot;

        public RobotEventHandler(RobotHardware robot) {
            this.robot = robot;
        }

        @EventSubscribe
        public void receive(DistanceSensorEvent event) {
            LOG.info("Received a distance event: {}", event);

//            if(event.getDistance() < 30) {
//                LOG.info("Stopping walking motion");
//                robot.getMotionEngine().stopWalking();
//            }
        }

        @EventSubscribe
        public void receive(TextualSensorEvent event) {
            LOG.info("Barcode scanned: {}", event.getValue());

            robot.getCapability(NaoSpeechEngine.class).say(event.getValue(), "english");
        }

        @EventSubscribe
        public void receive(BumperEvent event) {
            String source = event.getValue().getSource();
            if(event.isTriggered()) {
                LOG.info("Head was touched on: {}", event.getAttribute());
                if(source.equalsIgnoreCase("MiddleTactilTouched")) {
                    robot.getCapability(SpeechEngine.class).say("Someone just touched my head", "english");
                }
            }
        }
    }

}
