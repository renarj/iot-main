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
package com.oberasoftware.robo.rover;

import com.oberasoftware.max.core.CoreConfiguration;
import com.oberasoftware.max.web.MaxWebConfiguration;
import com.oberasoftware.robo.cloud.RemoteConfiguration;
import com.oberasoftware.robo.dynamixel.DynamixelConfiguration;
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
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        DynamixelConfiguration.class,
        RemoteConfiguration.class,
        SensorConfiguration.class,
        CoreConfiguration.class,
//        HexapodConfiguration.class,
        MaxWebConfiguration.class,
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
        initializer.initialize();

//        Walker walker = context.getBean(Walker.class);
//        LOG.info("Setting initial leg position");
//        walker.walk(new PosData(0, 0, 2), new PosData(0, 0, 0));
//        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
//
////        for(int i=0; i<10; i++) {
//            walker.walkDirection();
////            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
////        }
//
//        LOG.info("Done");
    }
}
