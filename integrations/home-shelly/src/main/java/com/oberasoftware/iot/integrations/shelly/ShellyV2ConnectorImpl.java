package com.oberasoftware.iot.integrations.shelly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.util.HttpUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ShellyV2ConnectorImpl implements ShellyConnector {
    private static final Logger LOG = getLogger(ShellyV2ConnectorImpl.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String URL_PATTERN = "http://%s/rpc/";

    private HttpClient client;

    @PostConstruct
    public void postConstruct() {
        this.client = HttpUtils.createClient(false);
    }

    @Override
    public ShellyMetadata getShellyInfo(String controllerId, String thingId, String shellyIp) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL_PATTERN, shellyIp) + "Shelly.GetDeviceInfo"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();
            LOG.info("Retrieved Shelly device info: {}", body);

            var root = OBJECT_MAPPER.readTree(body);
            var app = root.findValue("app").asText();
            var name = root.findValue("name").asText();

            return new ShellyMetadata(controllerId, thingId, shellyIp, ShellyMetadata.SHELLY_VERSION.V2,
                    name, app, ShellyDeviceComponents.forTypeName(app).getComponents());
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request things from service", e);
        }
    }

    @Override
    public Map<String, Value> getValues(String shellyIp, List<String> components) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL_PATTERN, shellyIp) + "Shelly.GetStatus"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();

            var root = OBJECT_MAPPER.readTree(body);
            var mappedValues = new HashMap<String, Value>();
            components.forEach(comp -> {
                var compNode = root.get(comp);

                compNode.fields().forEachRemaining(e -> {
                    var attribute = e.getKey();
                    var val = e.getValue();
                    var attributeKey = comp.replace(":", "") + "." + attribute;
                    if(val.isDouble()) {
                        mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.DECIMAL, val.asDouble()));
                    } else if(val.isInt() || val.isLong()) {
                        mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.NUMBER, val.asLong()));
                    } else {
                        mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.STRING, val.asText()));
                    }

                });
            });
            return mappedValues;
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request Shelly Device Status", e);
        }
    }

    @Override
    public boolean setRelay(String shellyIp, int relay, SwitchCommand.STATE state) throws IOTException {
        return false;
    }
}
