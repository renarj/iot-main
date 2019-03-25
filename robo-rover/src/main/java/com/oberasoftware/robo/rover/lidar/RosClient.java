package com.oberasoftware.robo.rover.lidar;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class RosClient {
    private static final Logger LOG = getLogger(RosClient.class);

    private final static ExecutorService executor = Executors.newFixedThreadPool(10);

    private WebSocket webSocket;


    public void connect() {
        HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();
        WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder().connectTimeout(Duration.of(30, ChronoUnit.SECONDS));

        try {

            webSocket = webSocketBuilder.buildAsync(URI.create("ws://10.1.0.49:9090"), new WebSocket.Listener() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    LOG.info("CONNECTED");

                    webSocket.sendText("{ \"op\": \"advertise\",\n" +
                            "  \"topic\": \"/base_scan\",\n" +
                            "  \"type\": \"sensor_msgs/LaserScan\"\n" +
                            "}", true).join();
                    LOG.info("Advertised topic");


                    WebSocket.Listener.super.onOpen(webSocket);
                }
                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    LOG.info("onText received with data " + data);
                    if(!webSocket.isOutputClosed()) {
                        webSocket.sendText("This is a message", true);
                    }
                    return WebSocket.Listener.super.onText(webSocket, data, last);
                }
                @Override
                public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                    LOG.info("Closed with status " + statusCode + ", reason: " + reason);
                    executor.shutdown();
                    return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                }

                @Override
                public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                    LOG.info("Binary");
                    return null;
                }

                @Override
                public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
                    LOG.info("PING");
                    return null;
                }

                @Override
                public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
                    LOG.info("PONG");
                    return null;
                }

                @Override
                public void onError(WebSocket webSocket, Throwable error) {
                    LOG.error("We had an error: {}", error.getMessage());
                }
            }).get();

        } catch (InterruptedException | ExecutionException e) {
            LOG.error("", e);
        }


        LOG.info("WebSocket created");
    }

    public void disconnect() {
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").thenRun(() -> LOG.info("Sent close"));
    }

    public void sendText(String data) {
        LOG.info("Sending data: {}", data);
        webSocket.sendText(data, true).join();

        LOG.info("Done sending");
    }

    public void sendLaserScan(LaserScan scan) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        try {
            String json = mapper.writeValueAsString(scan);


            String struct = "{ \"op\": \"publish\","
                    + "\"topic\": \"/base_scan\","
                    + "\"msg\": " + json + "}";
            LOG.info("Sending: {}", struct);

            sendText(struct);
        } catch (JsonProcessingException e) {
            LOG.error("", e);
        }
    }
}
