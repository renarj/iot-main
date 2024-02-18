package com.oberasoftware.home.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author renarj
 */
@Configuration
//@EnableJpaRepositories("com.oberasoftware.home.storage.jpa")
//@EntityScan("com.oberasoftware.iot.core.model.storage.impl")
@ComponentScan("com.oberasoftware.home.storage.jasdb")
@PropertySource("classpath:application.properties")
public class StorageConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
