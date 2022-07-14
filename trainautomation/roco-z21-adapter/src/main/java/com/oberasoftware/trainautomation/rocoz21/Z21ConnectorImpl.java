package com.oberasoftware.trainautomation.rocoz21;

import com.oberasoftware.base.event.EventFilter;
import com.oberasoftware.base.event.HandlerEntry;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.trainautomation.rocoz21.commands.Z21Command;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static com.oberasoftware.trainautomation.rocoz21.commands.Z21Command.bytesToHex;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Z21ConnectorImpl implements Z21Connector {
    private static final Logger LOG = getLogger( Z21ConnectorImpl.class );
    private static final int NOT_SET = -1;

    @Value("${z21.port:21106}")
    private int port;
    @Value("${z21.host}")
    private String z21Host;

    private final LocalEventBus eventBus;

    private DatagramSocket socket;

    private ConnectionListener listener;

    public Z21ConnectorImpl(LocalEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void initFilter() {
        eventBus.registerFilter(new EventFilter() {
            @Override
            public boolean isFiltered(Object event, HandlerEntry handler) {
                Method eventMethod = handler.getEventMethod();
                Z21ResponseFilter responseFilter = eventMethod.getAnnotation(Z21ResponseFilter.class);
                if(responseFilter != null && event instanceof Z21ReturnPacket pkg) {
                    int packageHeader = responseFilter.packageHeader();
                    int xHeader = responseFilter.xHeader();
                    int firstDataByte = responseFilter.firstDataByte();
                    byte[] data = pkg.getData();

                    boolean matching = true;
                    if(packageHeader != NOT_SET) {
                        matching = packageHeader == pkg.getResponseHeader();
                    }

                    if(xHeader != NOT_SET) {
                        matching = matching & xHeader == pkg.getxHeader();
                    }

                    if(firstDataByte != NOT_SET && data.length >= 2) {
                        matching = matching & firstDataByte == data[1];
                    }

                    return !matching;
                }
                return false;
            }
        });
    }

    @Override
    public void connect() throws HomeAutomationException {
        if(socket == null && listener == null) {
            try {
                LOG.info("Opening socket for Z21 connection on port: {}", port);
                socket = new DatagramSocket(port);
                listener = new ConnectionListener(eventBus, socket);
                listener.start();
            } catch (SocketException e) {
                throw new HomeAutomationException("Unable to open socket for Z21 connection", e);
            }
        } else {
            LOG.warn("Socket for Z21 connection already open");
        }
    }

    @Override
    public void disconnect() {
        if(socket != null && listener != null) {
            LOG.info("Disconnecting Z21 socket");
            listener.stop();
            socket.disconnect();
            listener = null;
            socket = null;
        }
    }

    @Override
    public void send(Z21Command command) throws HomeAutomationException {
        DatagramPacket packet = command.addHost(z21Host).addPort(port).build();

        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new HomeAutomationException("Unable to send Z21 command: " + command);
        }
    }

    private static class ConnectionListener implements Runnable {
        private DatagramSocket socket;

        private LocalEventBus eventBus;

        private boolean running = false;

        private ConnectionListener(LocalEventBus eventBus, DatagramSocket socket) {
            this.eventBus = eventBus;
            this.socket = socket;
        }

        public void start() {
            LOG.info("Starting receiver thread");
            running = true;
            new Thread(this).start();
        }

        public void stop() {
            LOG.info("Stopping receiver thread");
            running = false;
        }

        @Override
        public void run() {
            LOG.info("Receiver thread started");
            while(running) {
                DatagramPacket packet = new DatagramPacket(new byte [510], 510);
                try {
                    socket.receive(packet);

                    byte [] data = packet.getData();
                    LOG.info("Received data from Z21: {} socket: {}", bytesToHex(data), socket.getLocalPort());

                    Z21ReturnPacket dataPacket = new Z21ReturnPacket(data);
                    LOG.info("Parsed return data: {}", dataPacket);

                    //check if multiple packages arrived
                    if(data[dataPacket.getLength()] != 0x00) {
                        LOG.warn("We received a double package, no support for this yet");
                    }

                    this.eventBus.publish(dataPacket);
                } catch (IOException e) {
                    LOG.error("Error receiving datagram packet", e);
                }

            }

            LOG.info("Receiver thread finished");
        }
    }
}
