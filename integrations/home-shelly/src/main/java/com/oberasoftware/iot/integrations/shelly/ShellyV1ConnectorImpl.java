package com.oberasoftware.iot.integrations.shelly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class ShellyV1ConnectorImpl implements ShellyConnector {
    private static final Logger LOG = LoggerFactory.getLogger(ShellyV1ConnectorImpl.class);

    private static final String URL_PATTERN = "http://%s/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpClient client;

    @PostConstruct
    public void postConstruct() {
        this.client = HttpUtils.createClient(false);
    }

    public ShellyMetadata.SHELLY_VERSION getShellyVersion(String shellyIp) throws IOTException {
        try {
            var body = getResponse(shellyIp, "Shelly");
            LOG.info("Retrieved Shelly Version info: {}", body);

            var root = OBJECT_MAPPER.readTree(body);
            if(root.has("gen")) {
                var version = root.findValue("gen").asInt();

                return version == 2 ? ShellyMetadata.SHELLY_VERSION.V2 : ShellyMetadata.SHELLY_VERSION.V1;
            }

            return ShellyMetadata.SHELLY_VERSION.V1;
        } catch (IOException e) {
            throw new IOTException("Unable to process version data from Shelly device", e);
        }
    }

    private String getResponse(String shellyIp, String resource) throws IOTException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL_PATTERN, shellyIp) + resource))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var body = response.body();
            LOG.info("Retrieved Shelly Version info: {}", body);
            return body;
        } catch (IOException | InterruptedException e) {
            throw new IOTException("Unable to request version from Shelly device", e);
        }
    }

    @Override
    public ShellyMetadata getShellyInfo(String controllerId, String thingId, String shellyIp) throws IOTException {
        String settingsInfo = getResponse(shellyIp, "settings");
        try {
            var root = OBJECT_MAPPER.readTree(settingsInfo);
            var deviceNode = root.get("device");
            if(deviceNode.has("type") && deviceNode.has("hostname")) {
                String deviceType = deviceNode.get("type").asText();
                String hostName = deviceNode.get("hostname").asText();

                return new ShellyMetadata(controllerId, thingId, shellyIp, ShellyMetadata.SHELLY_VERSION.V1, hostName, deviceType, ShellyDeviceComponents.forTypeName(deviceType).getComponents());
            } else {
                throw new IOTException("Could not configure Shelly plug: " + thingId +", invalid response, no type or name available");
            }
        } catch (JsonProcessingException e) {
            throw new IOTException("Unable to request Settings from Shelly device", e);
        }


    }

    @Override
    public Map<String, Value> getValues(String shellyIp, List<String> components) throws IOTException {
        var response = getResponse(shellyIp, "status");
        LOG.info("Shelly: {} Status response: {}", shellyIp, response);

        try {
            var root = OBJECT_MAPPER.readTree(response);
            var mappedValues = new HashMap<String, Value>();
            components.forEach(comp -> {
                var compNode = root.get(comp);
                if (compNode.isArray()) {
                    processArray(comp, compNode, mappedValues);
                } else if(compNode.isNumber()) {
                    mappedValues.put(comp, new ValueImpl(VALUE_TYPE.NUMBER, compNode.asDouble()));
                }
            });
            return mappedValues;
        } catch (IOException e) {
            throw new IOTException("Unable to request Shelly Device Status", e);
        }
    }

    @Override
    public boolean setRelay(String shellyIp, int relay, SwitchCommand.STATE state) throws IOTException {
        return false;
    }

    private void processArray(String component, JsonNode compNode, Map<String, Value> valueMap) {
        for(int i=0; i<compNode.size(); i++) {
            JsonNode arrayNode = compNode.get(i);

            var componentKey = component + i;
            arrayNode.fields().forEachRemaining(e -> {
                mapValue(componentKey, e.getKey(), e.getValue(), valueMap);
            });
        }
    }

    private void mapValue(String component, String key, JsonNode value, Map<String, Value> mappedValues) {
        var attributeKey = component + "." + key;
        if(value.isDouble()) {
            mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.DECIMAL, value.asDouble()));
        } else if(value.isInt() || value.isLong()) {
            mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.NUMBER, value.asLong()));
        } else {
            mappedValues.put(attributeKey, new ValueImpl(VALUE_TYPE.STRING, value.asText()));
        }
    }
}
