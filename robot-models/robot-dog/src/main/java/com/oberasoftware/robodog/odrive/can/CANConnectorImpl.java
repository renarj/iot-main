package com.oberasoftware.robodog.odrive.can;

import com.oberasoftware.robodog.odrive.exception.ODriveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tel.schich.javacan.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thin lifecycle wrapper around a RawCanChannel using JavaCAN.
 */
public class CANConnectorImpl implements Closeable, CANConnector {
    private static final Logger LOG = LoggerFactory.getLogger(CANConnectorImpl.class);

    private final String iface;
    private RawCanChannel channel;
    private final AtomicBoolean opened = new AtomicBoolean(false);

    public CANConnectorImpl(String iface) {
        this.iface = iface;
    }

    @Override
    public synchronized void open() {
        if (opened.get()) {
            return;
        }
        try {
            JavaCANAutoDetect.initialize();
            channel = CanChannels.newRawChannel();
            channel.bind(NetworkDevice.lookup(iface));
            opened.set(true);
            LOG.info("ODrive CAN opened on interface {}", iface);
        } catch (Exception e) {
            throw new ODriveException("Failed to open CAN interface " + iface, e);
        }
    }

    @Override
    public boolean isOpen() {
        return opened.get();
    }

    public RawCanChannel channel() {
        if (!opened.get()) {
            throw new ODriveException("CAN interface not opened");
        }
        return channel;
    }

    @Override
    public void send(CanFrame frame) {
        try {
            channel().write(frame);
        } catch (IOException e) {
            throw new ODriveException("Failed to send CAN frame", e);
        }
    }

    @Override
    public CanFrame receive() {
        try {
            return channel().read();
        } catch (IOException e) {
            throw new ODriveException("Failed to read CAN frame", e);
        }
    }

    @Override
    public synchronized void close() {
        if (opened.compareAndSet(true, false)) {
            try {
                channel.close();
                LOG.info("ODrive CAN closed on interface {}", iface);
            } catch (IOException e) {
                LOG.warn("Error closing CAN channel: {}", e.getMessage());
            }
        }
    }
}
