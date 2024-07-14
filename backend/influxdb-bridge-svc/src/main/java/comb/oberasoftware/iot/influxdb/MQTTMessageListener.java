package comb.oberasoftware.iot.influxdb;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPathParser;
import com.oberasoftware.home.core.mqtt.ParsedPath;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MQTTMessageListener implements EventHandler {
    private static final Logger LOG = getLogger( MQTTMessageListener.class );

    private ConcurrentLinkedQueue<MQTTMessage> messages = new ConcurrentLinkedQueue<>();

    @Value("${writerThreads:2}")
    private Integer nrThreads;

    @PostConstruct
    public void postConstruct() {
        LOG.info("Starting Influx Writer threads, number of threads: {}", nrThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(nrThreads);

        for(int i=0; i<nrThreads; i++) {
            executorService.submit(() -> {
                LOG.info("Writer thread has started");
                while(!Thread.interrupted()) {
                    try {
                        MQTTMessage message = messages.poll();
                        if (message != null) {
                            write(message);
                        } else {
                            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
                        }
                    } catch(Exception e) {
                        LOG.error("Unknown exception took place", e);
                    }
                }
                LOG.info("Writer thread has stopped");
            });
        }
    }


    @Autowired
    private InfluxWriter influxWriter;

    @EventSubscribe
    public void receive(MQTTMessage message) {
        LOG.info("Received a MQTT message: {}", message);
        ParsedPath parsedPath = MQTTPathParser.parsePath(message.getTopic());

        if(parsedPath != null) {
            messages.add(message);
        } else {
            LOG.warn("Invalid MQTT message received: {}", message);
        }
    }

    public void write(MQTTMessage message) {
        ParsedPath parsedPath = MQTTPathParser.parsePath(message.getTopic());

        try {
            double val = Double.parseDouble(message.getMessage());

            influxWriter.write(parsedPath.getControllerId(), parsedPath.getDeviceId(), parsedPath.getLabel(), val);
        } catch(NumberFormatException e) {
            LOG.error("Not a Number value, ignoring message: " + message, e);
        }
    }
}
