package com.oberasoftware.home.hue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.util.HttpUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;


@Component
public class HueBridgeDiscoveryService {
    private static final Logger LOG = getLogger( HueBridgeDiscoveryService.class );

    public Set<HueBridge> startBridgeSearch() {
        LOG.info("No bridges connected yet, doing discovery first");

        LOG.info("Doing mDSN Discovery");
        Set<HueBridge> bridges = doMDNSDiscovery();
        LOG.info("Doing Online Discovery");
//        bridges.addAll(doOnlineDiscovery());
        LOG.info("Discovery finished, found: {} bridges", bridges.size());

        return bridges;
    }

    public CompletableFuture<Optional<String>> getApiKey(HueBridge bridge) {
        Supplier<Optional<String>> apiKeySupplier = () -> {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode user = mapper.createObjectNode();
            user.put("devicetype", "iotmain#" + "myrandomid");

            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
                LOG.info("Posting json: {}", json);
                HttpClient client = HttpUtils.createClient(true);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://" + bridge.getBridgeIp() + "/api"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                for(int triesLeft = 30; triesLeft > 0; --triesLeft) {
                    LOG.info("Please push the button on the Hue Bridge: {} now (" + triesLeft + " seconds left).", bridge.getBridgeIp());

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    LOG.info("Response on new user: {} for bridge: {}", response.body(), bridge.getBridgeIp());
                    if (response.body().contains("link button")) {
                        LOG.info("Link button not pressed for bridge: {}, 1 second sleep", bridge.getBridgeIp());

                        Uninterruptibles.sleepUninterruptibly(Duration.of(1, ChronoUnit.SECONDS));
                    } else if (response.body().contains("success")) {
                        JsonNode node = mapper.readTree(response.body());
                        JsonNode firstNode = node.iterator().next();
                        JsonNode resultNode = firstNode.get("success");
                        String userName = resultNode.get("username").asText();
                        LOG.info("Authenticated with user: {} on bridge: {}", userName, bridge.getBridgeIp());

                        return Optional.of(userName);
                    } else {
                        LOG.error("We got an error on hue bridge: {} authentication aborting", bridge.getBridgeIp());
                        triesLeft = -1;
                    }
                }
            } catch(IOException | InterruptedException e) {
                LOG.error("Could not connect to Hue Bridge", e);
            }

            return Optional.empty();
        };
        return CompletableFuture.supplyAsync(apiKeySupplier);
    }

    private static Set<HueBridge> doMDNSDiscovery() {
        return new HashSet<>(new MDNSDiscoveryService().discoverBridges());
    }

    private static Set<HueBridge> doOnlineDiscovery() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://discovery.meethue.com/")).build();

        Set<HueBridge> bridges = new HashSet<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.info("Response: {}", response.body());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            if(node.isArray()) {
                for (JsonNode bridge : node) {
                    String bridgeId = bridge.get("id").asText();
                    String bridgeIp = bridge.get("internalipaddress").asText();
                    int bridgePort = bridge.get("port").asInt();

                    LOG.info("Found a bridge: {} with ip: {} on port: {}", bridgeId, bridgeIp, bridgePort);
                    bridges.add(new HueBridge(bridgeId, bridgeIp, bridgePort));
                }
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("", e);
        }
        return bridges;
    }
}
