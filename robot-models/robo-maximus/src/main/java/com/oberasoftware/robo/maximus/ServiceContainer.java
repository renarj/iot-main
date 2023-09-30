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

import com.oberasoftware.iot.client.ClientConfiguration;
import com.oberasoftware.iot.core.robotics.humanoid.cartesian.CartesianControl;
import com.oberasoftware.max.core.CoreConfiguration;
import com.oberasoftware.robo.cloud.RemoteConfiguration;
import com.oberasoftware.robo.dynamixel.DynamixelConfiguration;
import com.oberasoftware.robo.dynamixel.SerialDynamixelConnector;
import com.oberasoftware.robo.dynamixel.web.WebConfiguration;
import com.oberasoftware.robo.pi4j.SensorConfiguration;
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

/**
 * @author rdevries
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
@Import({
        DynamixelConfiguration.class,
        RemoteConfiguration.class,
        SensorConfiguration.class,
        CoreConfiguration.class,
        ClientConfiguration.class,
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
//        var topicBus = context.getBean(MQTTTopicEventBus.class);
//        topicBus.initialize();
//        topicBus.connect();

//        initializer.initialize();

        initializer.initialize((robot, humanoidRobot) -> {
            CartesianControl cartesianControl = humanoidRobot.getBehaviour(CartesianControl.class);
//            cartesianControl.move(TORSO, 0, 0, -200, 5000, TimeUnit.MILLISECONDS);

//            DynamixelServoDriver servoDriver = (DynamixelServoDriver)robot.getServoDriver();
//            servoDriver.scan(false);

//            cartesianControl.move(TORSO, 0, 0, 260, 5000, TimeUnit.MILLISECONDS);
        }, false);



        LOG.info("Done");
    }
}
