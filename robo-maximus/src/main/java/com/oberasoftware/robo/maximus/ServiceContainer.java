/**
 * Copyright (c) 2015 SDL Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oberasoftware.robo.maximus;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.max.core.CoreConfiguration;
import com.oberasoftware.robo.api.commands.Scale;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.cloud.RemoteConfiguration;
import com.oberasoftware.robo.core.commands.RebootCommand;
import com.oberasoftware.robo.dynamixel.DynamixelConfiguration;
import com.oberasoftware.robo.dynamixel.DynamixelInstruction;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.SerialDynamixelConnector;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2Address;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.web.WebConfiguration;
import com.oberasoftware.robo.pi4j.SensorConfiguration;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;

/**
 * @author rdevries
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, JmxAutoConfiguration.class})
@Import({
        DynamixelConfiguration.class,
        RemoteConfiguration.class,
        SensorConfiguration.class,
        CoreConfiguration.class,
//        MaxWebConfiguration.class,
        WebConfiguration.class

})
@ComponentScan
public class ServiceContainer {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceContainer.class);


    public static void main(String[] args) {
        LOG.info("Starting Robot Service Application container");



        SpringApplication springApplication = new SpringApplication(ServiceContainer.class);
        ConfigurableApplicationContext context = springApplication.run(args);
        RobotInitializer initializer = context.getBean(RobotInitializer.class);
        SerialDynamixelConnector d = context.getBean(SerialDynamixelConnector.class);

//122: {"command":"dynamixel","wait":"true","dxldata":"FFFFFD007A0300010D12"}

        //132: {"command":"dynamixel","wait":"true","dxldata":"FFFFFD0084030001268A"}

//        d.sendAndReceive(new DynamixelV2CommandPacket(DynamixelInstruction.PING, 122).build());

        initializer.initialize();

//        initializer.initialize((r, max) -> {
//            LOG.info("Rebooting servos");
//            RobotInitializer.SERVO_IDS.forEach(s -> r.getServoDriver().resetServo(s));
//        }, true);

//        initializer.initialize((r, max) -> {
////            max.getMotionControl().runMotion("wave");
//            Servo s130 = max.getRobotCore().getServoDriver().getServo("130");
//            Servo s120 = max.getRobotCore().getServoDriver().getServo("120");
//
//            s130.moveTo(2000, new Scale(0, 4000));
//            s120.moveTo(2000, new Scale(0, 4000));
//        }, true);

//        initializer.initialize((r, max) -> {
//            byte[] rcvd = d.sendAndReceive(new DynamixelV2CommandPacket(DynamixelInstruction.WRITE_DATA, 101)
//                            .add8BitParam(DynamixelV2Address.TORGUE_ENABLE, 1).build());
//            LOG.info("Received: {}", bb2hex(rcvd));
//
//
//            rcvd = d.sendAndReceive(new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, 101)
//                    .addParam(DynamixelV2Address.GOAL_POSITION_L, (byte)0x04, (byte)0x00).build());
//            LOG.info("Received: {}", bb2hex(rcvd));
//
//        }, true);

        LOG.info("Done");
    }
}
