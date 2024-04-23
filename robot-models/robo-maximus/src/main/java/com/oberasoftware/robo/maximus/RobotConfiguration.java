package com.oberasoftware.robo.maximus;

import com.oberasoftware.iot.core.extensions.SpringExtension;
import com.oberasoftware.robo.core.CoreConfiguration;
import com.oberasoftware.robo.dynamixel.DynamixelConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({CoreConfiguration.class, DynamixelConfiguration.class})
public class RobotConfiguration implements SpringExtension {
}
