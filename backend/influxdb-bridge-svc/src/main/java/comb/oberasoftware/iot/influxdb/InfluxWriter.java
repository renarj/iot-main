package comb.oberasoftware.iot.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class InfluxWriter {
    private static final Logger LOG = getLogger( InfluxWriter.class );

    @Value("${influxUrl:}")
    private String influxUrl;

    @Value("${influxToken:}")
    private String influxToken;

    @Value("${influxOrg:}")
    private String influxOrg;

    @Value("${bucket:}")
    private String bucket;

    private InfluxDBClient influxDBClient;

    public void connect() {
        LOG.info("Connecting to InfluxDB: {} on org: {} with bucket: {}", influxUrl, influxOrg, bucket);
        influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(),
                influxOrg, bucket);
    }

    public void write(String controllerId, String thingId, String label, Object value) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("things")
                .addTag("controllerId", controllerId)
                .addTag("thingId", thingId)
                .addTag("label", label);

        if(value instanceof Integer) {
            point.addField("value", ((Integer)value).doubleValue());
        } else {
            point.addField("value", (double)value);
        }
        point.time(Instant.now().toEpochMilli(), WritePrecision.MS);

        LOG.debug("Writing value: {} for {}/{}/{} to metrics DB", value, controllerId, thingId, label);
        writeApi.writePoint(point);
    }
}
