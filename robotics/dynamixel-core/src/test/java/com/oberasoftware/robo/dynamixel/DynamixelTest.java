package com.oberasoftware.robo.dynamixel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.robotics.commands.TorgueCommand;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Renze de Vries
 */
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
@Import(DynamixelConfiguration.class)
public class DynamixelTest {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelTest.class);

    private static final List<String> SERVO_IDS = Lists.newArrayList("101", "109", "110", "122");
    private static final String MOTOR_ID_STRING = String.join(",", SERVO_IDS);

    public static void main(String[] args) {
        LOG.info("Starting up");
        ApplicationContext context = SpringApplication.run(DynamixelTest.class);
        ServoDriver servoDriver = context.getBean(ServoDriver.class);
        servoDriver.activate(null, ImmutableMap.<String, String>builder()
                .put(DynamixelServoDriver.PORT, "/dev/tty.usbmodem102")
                .put("motors", MOTOR_ID_STRING)
                .put("protocol.v2.enabled", "true").build());

        servoDriver.sendCommand(new TorgueCommand("101", true));
        servoDriver.sendCommand(new TorgueCommand("109", true));
        servoDriver.sendCommand(new TorgueCommand("110", true));
        servoDriver.sendCommand(new TorgueCommand("122", true));

        LOG.info("Servos found");
        servoDriver.getServos().forEach(s -> {
            LOG.info("Servo found: {} on position: {}", s.getId(), s.getData().getValue(ServoProperty.POSITION));
        });

        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
        servoDriver.sendCommand(new TorgueCommand("101", false));
        servoDriver.sendCommand(new TorgueCommand("109", false));
        servoDriver.sendCommand(new TorgueCommand("110", false));
        servoDriver.sendCommand(new TorgueCommand("122", false));


//        servoDriver.sendCommand(new AngleLimitCommand("6", AngleLimitCommand.MODE.WHEEL_MODE));
//        servoDriver.sendCommand(new AngleLimitCommand("14", AngleLimitCommand.MODE.WHEEL_MODE));
//        servoDriver.sendCommand(new AngleLimitCommand("16", AngleLimitCommand.MODE.WHEEL_MODE));
//        servoDriver.sendCommand(new AngleLimitCommand("6", AngleLimitCommand.MODE.WHEEL_MODE));

//        servoDriver.setServoSpeed("6", 300);
//        servoDriver.setServoSpeed("14", 300);
//        servoDriver.setServoSpeed("16", 300);
//        servoDriver.setServoSpeed("6", 300);
        Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);
        System.exit(-1);
//        servoDriver.setServoSpeed("6", 0);
//        servoDriver.setServoSpeed("14", 0);
//        servoDriver.setServoSpeed("16", 0);
//        servoDriver.setServoSpeed("6", 0);

    }


}
