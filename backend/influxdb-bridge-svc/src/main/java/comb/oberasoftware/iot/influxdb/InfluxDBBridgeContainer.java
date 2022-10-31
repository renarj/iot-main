package comb.oberasoftware.iot.influxdb;

import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
@Import(MQTTConfiguration.class)
public class InfluxDBBridgeContainer {
    private static final Logger LOG = getLogger( InfluxDBBridgeContainer.class );

    public static void main(String[] args) {
        LOG.info("Starting InfluxDB Bridge container");

        ApplicationContext context = new SpringApplication(InfluxDBBridgeContainer.class)
                .run(args);
        var bootstrap = context.getBean(MQTTBootstrap.class);
        bootstrap.configure();

        LOG.info("InfluxDB Bridge container started");
    }
}
