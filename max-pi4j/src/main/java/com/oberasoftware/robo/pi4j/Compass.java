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
public class Compass {
    private static final Logger LOG = LoggerFactory.getLogger(Compass.class);

    private final GrovePort port;

    public Compass(GrovePort port) {
        this.port = port;
        //register mode
//        port.write(0x02, 0);
    }

    public void test() throws IOException {
        //register mode???
        I2CDevice device = port.getDevice();

        device.write(0x02, (byte)0x00);


        for(int i=0; i<50; i++) {
            byte[] buffer = new byte[6];
            device.read(0x03, buffer, 0, 6);

            LOG.info("Read data {}", buffer);

            int x = (buffer[0] << 8) + buffer[1];

//            if( (x&(1<<(16-1))) != 0 ) {
//                x = x - (1 << 16);
//            }
            int z = (buffer[2] << 8) + buffer[3];
//            int y = (buffer[4] << 8) + buffer[5];
//            int y = buffer[4] * 256 + buffer[5];

//            if( (y&(1<<(16-1))) != 0 ) {
//                y = y - (1 << 16);
//            }


//            int xval = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);
//            int zval = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
            int y = ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);
            double heading = Math.atan2(y, x);
//            if(heading < 0) {
//                heading = heading + (2 * Math.PI);
//            } else {
//                heading = heading - (2 * Math.PI);
//            }
            double degrees = Math.round(Math.toDegrees(heading));


            LOG.info("X: {} Y: {} Z: {} Heading: {} degrees: {}", x, y, z, heading, degrees);

            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }
    }

    public int getHeading() {
        return -1;
    }
}
