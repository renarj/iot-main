package com.oberasoftware.robo.dynamixel;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1CommandPacket.bb2hex;


/**
 * @author Renze de Vries
 */
@Component
public class SerialDynamixelConnector implements DynamixelConnector {
    private static final Logger LOG = LoggerFactory.getLogger(SerialDynamixelConnector.class);

    private SerialPort serialPort;

    private String portName;

    private List<Byte> buffer = new CopyOnWriteArrayList<>();
    private int messageLength = 0;

    private AtomicBoolean responseReceived = new AtomicBoolean(false);

    @Value("${dynamixel.baudrate:57600}")
    protected int baudRate = 57600;

    private final Lock lock = new ReentrantLock();

    /*
     * Response delay time in ms. before reading buffer for a serial response
     */
    protected int responseDelayTime = 400;

    @Override
    public void connect(String portName) {
        this.portName = portName;
        LOG.info("Connecting to serial port: {}", portName);

        serialPort = new SerialPort(portName);
        try {
            boolean opened = serialPort.openPort();
            LOG.debug("Port: {} opened: {}", portName, opened);

            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            serialPort.setParams(baudRate, 8, 1, 0);
            serialPort.purgePort(SerialPort.PURGE_TXCLEAR);

            if(opened) {
                LOG.debug("Registered event listener");
                serialPort.addEventListener(new DynamixelSerialEventListener());
            }
        } catch (SerialPortException e) {
            LOG.error("Unable to open serial port", e);
        }
    }

    @Override
    public void disconnect() {
        LOG.info("Disconnecting from serial port: {}", portName);

        try {
            boolean closed = serialPort.closePort();
            LOG.info("Closed port: {} successfully: {}", portName, closed);
        } catch (SerialPortException e) {
            LOG.error("Unable to close port to dynamixel controller", e);
        }
    }

    @Override
    public synchronized byte[] sendAndReceive(byte[] bytes) {
        return send(bytes, true);
    }

    @Override
    public synchronized void sendNoReceive(byte[] bytes) {
        send(bytes, false);
    }

    protected synchronized byte[] send(byte[] bytes, boolean wait) {
        LOG.debug("Sending message: {} waiting for response: {}", bb2hex(bytes), wait);

        lock.lock();
        try {
            resetBuffer();

            boolean writeSuccess = serialPort.writeBytes(bytes);
            if(writeSuccess && wait) {

                waitForResponse();
                return readBytes();
            }

            if(!writeSuccess) {
                LOG.warn("Write was not successful of bytes: {}", bb2hex(bytes));
            }
        } catch (SerialPortException e) {
            LOG.error("Unable to write message to dynamixel controller", e);
        } finally {
            lock.unlock();
        }

        return new byte[0];
    }

    private void waitForResponse() {
        long start = System.currentTimeMillis();
        while(!responseReceived.get() && !Thread.currentThread().isInterrupted() && (System.currentTimeMillis() - start) < responseDelayTime) {
            sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
        }
    }

    private byte[] readBytes() {
        List<Byte> copyBytes = new ArrayList<>(buffer);
        buffer.clear();

        byte[] bu = new byte[copyBytes.size()];
        for(int i=0; i<copyBytes.size(); i++) {
            bu[i] = copyBytes.get(i);
        }
        responseReceived.set(false);
        return bu;
    }

    private void resetBuffer() {
        buffer.clear();

        try {
            serialPort.readBytes();
        } catch (SerialPortException e) {
            LOG.error("Could not read bytes", e);
        }
    }

    private class DynamixelSerialEventListener implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            LOG.debug("Bytes in buffer: {}", serialPortEvent.getEventValue());
            try {
                if(buffer.isEmpty() && messageLength == 0) {
                    byte[] header = serialPort.readBytes(4);
                    messageLength = Integer.parseInt(new String(header));
                    LOG.debug("Message length: {}", messageLength);
                }

                byte[] readBytes = serialPort.readBytes();
                if(readBytes!=null && readBytes.length > 0) {
                    for (byte b : readBytes) {
                        buffer.add(b);
                    }
                    LOG.trace("This is in the buffer: {}", new String(readBytes));

                    if(buffer.size() == messageLength) {
                        LOG.debug("Message complete");
                        messageLength = 0;
                        responseReceived.set(true);
                    } else {
                        LOG.debug("Buffer not yet received full message, buffer: {}, expected: {}, waiting", buffer.size(), messageLength);
                    }
                }
            } catch (SerialPortException e) {
                LOG.error("", e);
            }
        }
    }
}
