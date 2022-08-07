package com.oberasoftware.home.core.mqtt;

import com.oberasoftware.base.BaseConfiguration;
import com.oberasoftware.iot.core.CoreConfiguation;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Renze de Vries
 */
@Configuration
@Import({CoreConfiguation.class, BaseConfiguration.class})
@ComponentScan
public class MQTTConfiguration {
}
