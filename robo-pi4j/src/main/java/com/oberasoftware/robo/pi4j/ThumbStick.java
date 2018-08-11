package com.oberasoftware.robo.pi4j;

import com.google.common.util.concurrent.Uninterruptibles;
import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author renarj
 */
public class ThumbStick {
    private static final Logger LOG = LoggerFactory.getLogger(ThumbStick.class);

    private final GrovePort port;

    private I2CDevice device;

    public ThumbStick(GrovePort port) {
        this.port = port;
        //register mode
//        port.write(0x02, 0);
    }

    public void test() throws IOException {
        //register mode???
        device = port.getDevice();
//        device.write(0x02, (byte)0x00);

        //set pinmode to 0 (input) for pin 0
        write(true, 5, 0, 0, 0);
        write(true, 5, 1, 0, 0);
//        device.write(new byte[] {0x05, 0x00, 0x00, 0x00});
        //set pinmode to 0 (input) for pin 1
//        device.write(new byte[] {0x05, 0x01, 0x00, 0x00});
        Uninterruptibles.sleepUninterruptibly(300, TimeUnit.MILLISECONDS);
//        write(5, 3, 0, 0);
//        Uninterruptibles.sleepUninterruptibly(300, TimeUnit.MILLISECONDS);


        LOG.info("Finished setting pins to input mode");

        for(int i=0; i<10; i++) {
//            byte[] buffer = new byte[6];
            //prep pin 1 for reading
            byte[] pinY = readPin(device, 0);
            byte[] pinX = readPin(device, 1);





            int[] vy = unsign(pinY);
            int y = (vy[1] * 256) + vy[2];
            LOG.info("Read data for pinY: {} value: {}", pinY, y);

            int[] vx = unsign(pinX);
            int x = (vx[1] * 256) + vx[2];
            LOG.info("Read data for pinX: {} value: {}", pinX, x);

//            LOG.info("Read data for pinY: {}", pinY);

//            int x = (buffer[0] << 8) + buffer[1];
//
////            if( (x&(1<<(16-1))) != 0 ) {
////                x = x - (1 << 16);
////            }
//            int z = (buffer[2] << 8) + buffer[3];
////            int y = (buffer[4] << 8) + buffer[5];
////            int y = buffer[4] * 256 + buffer[5];
//
////            if( (y&(1<<(16-1))) != 0 ) {
////                y = y - (1 << 16);
////            }
//
//
////            int xval = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);
////            int zval = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
//            int y = ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);
//            double heading = Math.atan2(y, x);
////            if(heading < 0) {
////                heading = heading + (2 * Math.PI);
////            } else {
////                heading = heading - (2 * Math.PI);
////            }
//            double degrees = Math.round(Math.toDegrees(heading));
//
//
//            LOG.info("X: {} Y: {} Z: {} Heading: {} degrees: {}", x, y, z, heading, degrees);
//
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }

    public void write(boolean shouldRetry, int... data) throws IOException {
        byte[] buffer = new byte[data.length];
        for(int i=0; i<data.length; i++) {
            buffer[i] = (byte)data[i];
        }
        LOG.info("Writing data: {}", buffer);

        boolean success = false;
        for(int i=0; i<10 && !success; i++) {
            try {
                device.write(1, buffer);
                success = true;
                LOG.warn("Success after attempt: {}", i);
            } catch(IOException e) {
                if(!shouldRetry) {
                    throw new IOException("Could not write, no retry");
                } else {
                    LOG.warn("Error on write of data: {}", data);
                    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
                }
            }
        }
        if(!success) {
            throw new IOException("Could not write");
        }
    }

    public byte[] read() throws IOException {
        byte[] buffer = new byte[4];

        int r = device.read(1);
        LOG.info("We got: {}", r);

        device.read(1,  buffer, 0, 4);

        return buffer;
    }

    public byte[] readPin(I2CDevice device, int pin) throws IOException {

        for(int i=0; i<10; i++) {
            try {
                write(false, 3, pin, 0, 0);
                LOG.info("Pin: {} is ready for read", pin);
                Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
                byte[] xPin = read();

                LOG.info("Received buffer: {}", xPin);
                return xPin;
            } catch(IOException e) {
                LOG.warn("Could not read pin: {}, retrying", pin);
                Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
            }
        }

        throw new IOException("Could not read pin: " + pin);
    }

    public int getHeading() {
        return -1;
    }

    public static int[] unsign(byte[] b) {
        int[] v = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            v[i] = unsign(b[i]);
        }
        return v;
    }

    public static int unsign(byte b) {
        return b & 0xFF;
    }

}
