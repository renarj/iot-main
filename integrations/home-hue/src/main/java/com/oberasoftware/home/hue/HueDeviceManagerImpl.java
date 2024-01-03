package com.oberasoftware.home.hue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.legacymodel.OnOffValue;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.AttributeType;
import com.oberasoftware.iot.core.model.storage.impl.ThingBuilder;
import com.oberasoftware.iot.core.util.HttpUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AgentControllerInformation agentControllerInformation;

    public HueDeviceManagerImpl(HueConnector hueConnector) {
        this.hueConnector = hueConnector;
    }

    @Override
    public List<IotThing> getDevices(String bridgeId) {
        HueBridge bridge = hueConnector.getBridge(bridgeId);

        List<IotThing> devicesFound = new ArrayList<>();
        doLightRetrieval(bridge, light -> {
            JsonNode lightInfo = light.getValue();
            var dev = createDeviceModel(bridge.getBridgeId(), light.getKey(), lightInfo);
            LOG.debug("Found hue light: {} on bridge: {}", dev, bridge.getBridgeId());
            devicesFound.add(dev);
        });
        LOG.info("Found {} devices on bridges", devicesFound.size());
        return devicesFound;
    }

    private void doLightRetrieval(HueBridge bridge, Consumer<Map.Entry<String, JsonNode>> lightNodeConsumer) {
        HttpClient client = HttpUtils.createClient(true);

        LOG.info("Getting lights for bridge: {}", bridge);
        HttpRequest request = HttpRequest.newBuilder().uri(UriUtils.createUri(bridge, "lights")).build();
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
    public Optional<IotThing> findDevice(String bridgeId, String lightId) {
        HttpClient client = HttpUtils.createClient(true);
        var bridge = findBridge(bridgeId);
        if(bridge.isPresent()) {
            LOG.info("Getting light: {} for bridge: {}", lightId, bridge.get());
            HttpRequest request = HttpRequest.newBuilder().uri(UriUtils.createUri(bridge.get(), "lights/" + lightId)).build();

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

    private IotThing createDeviceModel(String bridgeId, String lightId, JsonNode lightInfo) {
        String lightName = lightInfo.get("name").asText();
        String lightType = lightInfo.get("productname").asText();

        return ThingBuilder.create(lightId, agentControllerInformation.getControllerId())
                .friendlyName(lightName)
                .type("light")
                .plugin(HueExtension.HUE_NAME)
                .parent(bridgeId)
                .addProperty("lightType", lightType)
                .addProperty("bridge", bridgeId)
                .addAttribute("on", AttributeType.SWITCH)
                .addAttributes("bri", "hue", "sat", "xy")
                .build();
    }

    private Optional<HueBridge> findBridge(String bridgeId) {
        return hueConnector.getBridges().stream().filter(b -> b.getBridgeId().equalsIgnoreCase(bridgeId)).findFirst();
    }

    @Override
    public boolean switchState(String bridgeId, String lightId, OnOffValue state) {
        HttpClient client = HttpUtils.createClient(true);
        var bridge = findBridge(bridgeId);

        if(bridge.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode user = mapper.createObjectNode();
            user.put("on", state.isOn());
            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(UriUtils.createUri(bridge.get(), "lights/" + lightId + "/state"))
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
