package com.oberasoftware.robo.maximus;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.slf4j.LoggerFactory.getLogger;

public class SimpleTest {
    private static final Logger LOG = getLogger(SimpleTest.class);

    private final static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void oldMain(String[] args) {
        LOG.info("Hello world");

        HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();

        WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder().connectTimeout(Duration.of(30, ChronoUnit.SECONDS));

        WebSocket webSocket = null;
        try {

            webSocket = webSocketBuilder.buildAsync(URI.create("ws://10.1.0.49:9090"), new WebSocket.Listener() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    LOG.info("CONNECTED");
//                    webSocket.sendText("This is a message", true);

                    webSocket.sendText("{ \"op\": \"subscribe\",\n" +
                            "  \"topic\": \"/turtle1/cmd_vel\",\n" +
                            "  \"type\": \"geometry_msgs/Twist\"\n" +
                            "}", true);


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
        sleepUninterruptibly(100, TimeUnit.SECONDS);
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").thenRun(() -> LOG.info("Sent close"));

    }


    public static void main(String[] args) {

        var f = new LinearInterpolator().interpolate(new double[] {40, 120, 180}, new double[] {0.0, 5.0, 8.0});

        double[] knots = f.getKnots();
        LOG.info("Knots: {}", knots);
    }
}
