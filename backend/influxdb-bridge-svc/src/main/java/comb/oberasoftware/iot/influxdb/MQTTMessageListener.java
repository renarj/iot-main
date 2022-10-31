package comb.oberasoftware.iot.influxdb;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPathParser;
import com.oberasoftware.home.core.mqtt.ParsedPath;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MQTTMessageListener implements EventHandler {
    private static final Logger LOG = getLogger( MQTTMessageListener.class );

    @Autowired
    private InfluxWriter influxWriter;

    @EventSubscribe
    public void receive(MQTTMessage message) {
        LOG.debug("Received a MQTT message: {}", message);
        ParsedPath parsedPath = MQTTPathParser.parsePath(message.getTopic());

        if(parsedPath != null) {
            try {
                double val = Double.parseDouble(message.getMessage());

                influxWriter.write(parsedPath.getControllerId(), parsedPath.getDeviceId(), parsedPath.getLabel(), val);
            } catch(NumberFormatException e) {
                LOG.debug("Not a Number value, ignoring message: " + message, e);
            }
        } else {
            LOG.warn("Invalid MQTT message received: {}", message);
        }
    }
}
