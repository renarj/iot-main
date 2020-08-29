package com.oberasoftware.robo.maximus;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.ActivatableCapability;
import com.oberasoftware.robo.api.Capability;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.events.RobotValueEvent;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class InfluxDBMetricsCapability implements ActivatableCapability, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBMetricsCapability.class);
    public static final String ROBOT_EVENTS = "sensor_data";

    @Value("${influxdb.host.uri:http://k8master:8086}")
    private String influxDBHostUri;

    @Value("${influxdb.name:SENSOR_DATA}")
    private String influxDBName;

    private InfluxDB client;

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        client = InfluxDBFactory.connect(influxDBHostUri, "root", "root");
        client.query(new Query(String.format("CREATE DATABASE %s", influxDBName)));
        client.setDatabase(influxDBName);

        robot.listen(new MetricsEventListener());
    }

    public class MetricsEventListener implements EventHandler {
        @EventSubscribe
        public void receive(RobotValueEvent event) {
            LOG.debug("Received robot data point: {} for attributes: {}", event.getSourcePath(), event.getAttributes());
            writeDataPoints(event);
        }
    }

    private void writeDataPoints(RobotValueEvent event) {
        event.getAttributes().forEach(a -> {
            Object value = event.getValue(a);
            if(value instanceof Double || value instanceof Integer) {
                Point.Builder builder = Point.measurement(ROBOT_EVENTS)
                        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .tag("source", event.getSourcePath())
                        .tag("attribute", a);

                if(value instanceof Integer) {
                    builder.addField("value", ((Integer)value).doubleValue());
                } else {
                    builder.addField("value", (double) value);
                }

                client.write(builder.build());
            } else {
                LOG.debug("Attribute: {} is not a double /int: {}", a, value);
            }
        });
    }

    @Override
    public void shutdown() {
        client.close();
    }
}
