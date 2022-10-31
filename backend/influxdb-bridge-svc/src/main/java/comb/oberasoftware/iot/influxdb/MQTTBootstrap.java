package comb.oberasoftware.iot.influxdb;

import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MQTTBootstrap {
    private static final Logger LOG = getLogger( MQTTBootstrap.class );

    @Value("${ingestionTopic:}")
    private String mqttTopic;

    @Autowired
    private MQTTTopicEventBus topicEventBus;

    @Autowired
    private InfluxWriter influxWriter;

    public void configure() {
        topicEventBus.initialize();
        topicEventBus.connect();

        LOG.info("Connecting to metrics DB");
        influxWriter.connect();

        LOG.info("Subscribing to: {}", mqttTopic);
        topicEventBus.subscribe(mqttTopic);
    }
}
