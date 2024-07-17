package com.oberasoftware.home.core.state;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.oberasoftware.iot.core.managers.StateStore;
import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.Value;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class InfluxDBStateStore implements StateStore {
    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBStateStore.class);

    @org.springframework.beans.factory.annotation.Value("${influxUrl:}")
    private String influxUrl;

    @org.springframework.beans.factory.annotation.Value("${influxToken:}")
    private String influxToken;

    @org.springframework.beans.factory.annotation.Value("${influxOrg:}")
    private String influxOrg;

    @org.springframework.beans.factory.annotation.Value("${bucket:}")
    private String bucket;

    private InfluxDBClient influxDBClient;

    @PostConstruct
    public void connect() {
        LOG.info("Connecting to InfluxDB: {} on org: {} with bucket: {}", influxUrl, influxOrg, bucket);
        influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(),
                influxOrg, bucket);
    }
    @Override
    public void store(String controllerId, String thingId, String attribute, Value value) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("things")
                .addTag("controllerId", controllerId)
                .addTag("thingId", thingId)
                .addTag("attribute", attribute);

        if(value.getValue() instanceof Integer) {
            point.addField("value", ((Integer)value.getValue()).doubleValue());
        } else {
            point.addField("value", (double)value.getValue());
        }
        point.time(Instant.now().toEpochMilli(), WritePrecision.MS);

        LOG.debug("Writing value: {} for {}/{}/{} to metrics DB", value, controllerId, thingId, attribute);
        writeApi.writePoint(point);
    }

    @Override
    public Map<String, State> getStates() {
        return Map.of();
    }

    @Override
    public State getState(String controllerId, String thingId) {
        return null;
    }

    @Override
    public SUPPORTED_OPERATIONS getSupportedOperations() {
        return SUPPORTED_OPERATIONS.WRITE;
    }
}
