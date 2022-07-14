package com.oberasoftware.home.hue;


import org.slf4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MDNSDiscoveryService {
    private static final Logger LOG = getLogger( MDNSDiscoveryService.class );

    private enum STATES {
        IDLE,
        SEARCHING,
        STOPPED,
        CRASHED
    }

    private static final int DISCOVERY_MESSAGE_COUNT = 5;
    private static final int PORT = 5353;
    private static final long MILLISECONDS_BETWEEN_DISCOVERY_MESSAGES = 950L;

    private final InetAddress multicastAddress;
    private MulticastSocket socket;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture requestSendTask;
    private final DatagramPacket requestPacket;
    private STATES state = STATES.IDLE;

    public MDNSDiscoveryService() {
        try {
            multicastAddress = InetAddress.getByName("224.0.0.251");
        } catch (final UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        requestPacket = createRequestPacket();
    }

    public Set<HueBridge> discoverBridges() {
        Set<HueBridge> bridges = new HashSet<>();

        try {
            startSocket();
            scheduleMessages();
            if (state == STATES.IDLE) {
                state = STATES.SEARCHING;
            }
            LOG.info("mDNS discoverer started");

            while (state == STATES.SEARCHING) {
                final byte[] buffer = new byte[8192];
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);

                    bridges.add(packetHandler(packet));
                } catch (final Exception e) {
                    if (state != STATES.STOPPED) {
                        LOG.error("Unable to process receive package", e);
                    }
                }
            }
        } finally {
            stop();
        }
        return bridges;
    }

    private void startSocket() {
        if (state == STATES.IDLE) {
            try {
                socket = new MulticastSocket();
                socket.setTimeToLive(100);
                TimeUnit.MILLISECONDS.sleep(100L);
                state = STATES.SEARCHING;
            } catch (final Exception e) {
                state = STATES.CRASHED;
                LOG.error("Unable to receive multicast message", e);
            }
        }
    }

    private void scheduleMessages() {
        if (state == STATES.SEARCHING) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            requestSendTask = scheduledExecutorService.schedule(() -> {
                try {
                    for (int i = 0; i < DISCOVERY_MESSAGE_COUNT; i++) {
                        LOG.info("Sending a discovery message");
                        socket.send(requestPacket);
                        TimeUnit.MILLISECONDS.sleep(MILLISECONDS_BETWEEN_DISCOVERY_MESSAGES);
                    }
                } catch (final IOException | InterruptedException e) {
                    state = STATES.CRASHED;
                    LOG.error("", e);
                } finally {
                    stop();
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
    }

    private void stop() {
        LOG.info("mDNS discoverer stopped");
        state = STATES.STOPPED;
        if (requestSendTask != null) {
            requestSendTask.cancel(true);
            scheduledExecutorService.shutdown();
        }
        if (socket != null) {
            socket.close();
        }
    }

    private HueBridge packetHandler(final DatagramPacket packet) {
        final MDNSResponseParser parser = new MDNSResponseParser(packet, Arrays.asList("_hue", "_tcp", "local"));
        final String ip = parser.parse();
        LOG.debug("Got MDNS response '{}' from '{}'", ip, packet.getAddress());

        return new HueBridge(ip, ip, 443);
    }

    private DatagramPacket createRequestPacket() {
        final byte[] content = new byte[]{
                (byte) 0xBE, (byte) 0xEF, // transaction ID
                0x00, 0x00, // flags
                0x00, 0x01, // number of questions
                0x00, 0x00, // number of answers
                0x00, 0x00, // number of authority resource records
                0x00, 0x00, // number of additional resource records
                0x04, '_', 'h', 'u', 'e',
                0x04, '_', 't', 'c', 'p',
                0x05, 'l', 'o', 'c', 'a', 'l',
                0x00, // terminator
                0x00, 0x0C, // type
                0x00, (byte) 0xFF  // class
        };
        return new DatagramPacket(content, content.length, multicastAddress, PORT);
    }
}
