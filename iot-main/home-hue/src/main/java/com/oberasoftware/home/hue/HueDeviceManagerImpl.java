package com.oberasoftware.home.hue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.home.api.model.Status;
import com.oberasoftware.iot.core.model.OnOffValue;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class HueDeviceManagerImpl implements HueDeviceManager {
    private static final Logger LOG = getLogger(HueDeviceManagerImpl.class);

    private final HueConnector hueConnector;

    public HueDeviceManagerImpl(HueConnector hueConnector) {
        this.hueConnector = hueConnector;
    }

    @Override
    public List<Device> getDevices() {
        List<HueBridge> bridges = hueConnector.getBridges();

        List<Device> devicesFound = new ArrayList<>();
        bridges.forEach(b -> doLightRetrieval(b, light -> {
            JsonNode lightInfo = light.getValue();
            var dev = createDeviceModel(b.getBridgeId(), light.getKey(), lightInfo);
            LOG.debug("Found hue light: {} on bridge: {}", dev, b.getBridgeId());
            devicesFound.add(dev);
        }));
        LOG.info("Found {} devices on bridges", devicesFound.size());
        return devicesFound;
    }


    private void doLightRetrieval(HueBridge bridge, Consumer<Map.Entry<String, JsonNode>> lightNodeConsumer) {
        HttpClient client = HttpUtils.createClient();

        LOG.info("Getting lights for bridge: {}", bridge);
        HttpRequest request = HttpRequest.newBuilder().uri(HttpUtils.createUri(bridge, "lights")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            if(node.fields().hasNext()) {
                node.fields().forEachRemaining(lightNodeConsumer);
            }
        } catch(IOException | InterruptedException e) {
            LOG.error("Could not retrieve light resources from Hue Bridge: {}", bridge);
        }
    }

    @Override
    public List<HueDeviceState> retrieveStates(String bridgeId) {
        var bridge = findBridge(bridgeId);
        List<HueDeviceState> states = new ArrayList<>();
        bridge.ifPresent(b -> doLightRetrieval(b, light -> {
            String lightId = light.getKey();
            JsonNode stateNode = light.getValue().get("state");
            var onOff = new OnOffValue(stateNode.get("on").asBoolean());
            int brightNess = stateNode.get("bri").asInt();

            LOG.debug("Found Hue Light: {} state: {} brightness: {} on bridge: {}", lightId, onOff, brightNess, bridgeId);

            states.add(new HueDeviceState(brightNess, onOff, lightId, bridgeId));
        }));

        return states;
    }

    @Override
    public Optional<Device> findDevice(String bridgeId, String lightId) {
        HttpClient client = HttpUtils.createClient();
        var bridge = findBridge(bridgeId);
        if(bridge.isPresent()) {
            LOG.info("Getting light: {} for bridge: {}", lightId, bridge.get());
            HttpRequest request = HttpRequest.newBuilder().uri(HttpUtils.createUri(bridge.get(), "lights/" + lightId)).build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode lightInfo = mapper.readTree(response.body());
                if(lightInfo.has("uniqueid")) {
                    return Optional.of(createDeviceModel(bridgeId, lightId, lightInfo));
                }
            } catch(IOException | InterruptedException e) {
                LOG.error("Could not retrieve light resources from Hue Bridge: {}", bridgeId);
            }
        } else {
            LOG.error("Could not find light: {} on bridge: {} as bridge is not registered", lightId, bridgeId);
        }

        LOG.info("Could not find Hue Device: {} on bridge: {}", lightId, bridgeId);
        return Optional.empty();
    }

    private HueDevice createDeviceModel(String bridgeId, String lightId, JsonNode lightInfo) {
        String lightName = lightInfo.get("name").asText();
        String lightType = lightInfo.get("productname").asText();

        Map<String, String> properties = new HashMap<>();
        properties.put("lightType", lightType);
        properties.put("bridge", bridgeId);

        return new HueDevice(lightId, lightName, Status.DISCOVERED, properties);
    }

    private Optional<HueBridge> findBridge(String bridgeId) {
        return hueConnector.getBridges().stream().filter(b -> b.getBridgeId().equalsIgnoreCase(bridgeId)).findFirst();
    }

    @Override
    public boolean switchState(String bridgeId, String lightId, OnOffValue state) {
        HttpClient client = HttpUtils.createClient();
        var bridge = findBridge(bridgeId);

        if(bridge.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode user = mapper.createObjectNode();
            user.put("on", state.isOn());
            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(HttpUtils.createUri(bridge.get(), "lights/" + lightId + "/state"))
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpResponse<Void> r = client.send(request, HttpResponse.BodyHandlers.discarding());
                return r.statusCode() == 200;
            } catch(IOException | InterruptedException e) {
                LOG.error("Could not send light: {} state: {} to bridge: {}", lightId, state, bridgeId);
            }
        } else {
            LOG.error("Could not find bridge: {} for sending command to light: {}", bridgeId, lightId);
        }
        return false;
    }
}
