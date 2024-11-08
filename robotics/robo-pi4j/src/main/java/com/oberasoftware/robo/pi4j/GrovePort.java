package com.oberasoftware.robo.pi4j;

import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class GrovePort {
    private static final Logger LOG = getLogger(GrovePort.class);

    private final I2CDevice device;

    public GrovePort(I2CDevice device) {
        this.device = device;
    }

    void write(int... bytes) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        for (int aByte : bytes) {
            byteBuffer.put((byte) aByte);
        }

        try {
            device.write(byteBuffer.array(), 0, byteBuffer.limit());



            //let the board have a bit of time to catch up
            sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            LOG.error("Unable to send to grove board");
        }
    }

    public I2CDevice getDevice() {
        return device;
    }

//    byte[] read() {
//
//
//        int read = -1;
//        try {
//            List<>
//            byte[] buffer = new byte[1024];
//            while((read = device.read(buffer, 0, buffer.length)) > -1) {
//
//            }
//        } catch (IOException e) {
//            LOG.error("", e);
//        }
//    }

}
