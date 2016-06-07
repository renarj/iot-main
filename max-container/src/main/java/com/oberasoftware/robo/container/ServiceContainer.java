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
package com.oberasoftware.robo.container;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.events.DistanceSensorEvent;
import com.oberasoftware.robo.cloud.RemoteCloudDriver;
import com.oberasoftware.robo.cloud.RemoteConfiguration;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.DistanceSensor;
import com.oberasoftware.robo.dynamixel.DynamixelConfiguration;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.pi4j.ADS1115Driver;
import com.oberasoftware.robo.pi4j.SensorConfiguration;
import com.oberasoftware.robo.service.MotionFunction;
import com.oberasoftware.robo.service.PositionFunction;
import com.oberasoftware.robo.service.ServiceConfiguration;
import com.oberasoftware.robo.service.model.MotionModel;
import com.oberasoftware.robo.service.model.ServoModel;
import com.oberasoftware.robomax.core.MaxCoreConfiguration;
import com.oberasoftware.robomax.core.RoboPlusClassPathResource;
import com.oberasoftware.robomax.core.RoboPlusMotionEngine;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.sdl.odata.service.ODataServiceConfiguration;
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

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author rdevries
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        DynamixelConfiguration.class,
        ServiceConfiguration.class,
        MaxCoreConfiguration.class,
        RemoteConfiguration.class,
        ODataServiceConfiguration.class,
        SensorConfiguration.class
})
@ComponentScan
public class ServiceContainer {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Robot Service Application container");

        SpringApplication springApplication = new SpringApplication(ServiceContainer.class);
        ConfigurableApplicationContext context = springApplication.run(args);

        ODataEdmRegistry registry = context.getBean(ODataEdmRegistry.class);
        registry.registerClasses(newArrayList(MotionModel.class, ServoModel.class, MotionFunction.class, PositionFunction.class));

        Robot robot = new SpringAwareRobotBuilder("max", context)
                .motionEngine(RoboPlusMotionEngine.class, new RoboPlusClassPathResource("/bio_prm_humanoidtypea_en.mtn"))
                .servoDriver(DynamixelServoDriver.class, ImmutableMap.<String, String>builder().put(DynamixelServoDriver.PORT, "/dev/tty.usbmodem1411").build())
                .sensor(new DistanceSensor("distance", "A0"), ADS1115Driver.class)
//                .sensor(new GyroSensor("gyro", adsDriver.getPort("A2"), adsDriver.getPort("A3"), new AnalogToPercentageConverter()))
                .remote(RemoteCloudDriver.class)
                .build();
        RobotEventHandler eventHandler = new RobotEventHandler(robot);
        robot.listen(eventHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }

    public static class RobotEventHandler implements EventHandler {
        private Robot robot;

        public RobotEventHandler(Robot robot) {
            this.robot = robot;
        }

        @EventSubscribe
        public void receive(DistanceSensorEvent event) {
            LOG.info("Received a distance event: {}", event);
        }
    }
}
