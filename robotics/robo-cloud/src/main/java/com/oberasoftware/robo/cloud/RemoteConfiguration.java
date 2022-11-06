package com.oberasoftware.robo.cloud;

import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import com.oberasoftware.iot.core.CoreConfiguation;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Renze de Vries
 */
@Configuration
@ComponentScan
@Import({CoreConfiguation.class, MQTTConfiguration.class})
public class RemoteConfiguration {
}
