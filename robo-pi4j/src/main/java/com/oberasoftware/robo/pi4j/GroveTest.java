package com.oberasoftware.robo.pi4j;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.robo.api.sensors.AnalogPort;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class GroveTest {
    private static final Logger LOG = getLogger(GroveTest.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting grove tests");

        ADS1115Driver d = new ADS1115Driver();
        d.activate(null, null);

//        for(int i=0; i<10; i++) {
//            List<AnalogPort> ports = d.getPorts();
//
//            ports.forEach(p -> {
//                LOG.info("Port: {}", p.);
//            });
//
        LOG.info("Sleeping");
            Uninterruptibles.sleepUninterruptibly(200, TimeUnit.SECONDS);
//        }


//        GroveBoard board = new GroveBoard();
//        board.activate(null, null);

//        board.getThumbStick().test();
//        RGBLed leftLed = board.getLed(7);
//        RGBLed rightLed = board.getLed(8);
//
//        for(int i=0; i<10; i++) {
//            if(i % 2 == 0) {
//                LOG.info("Left light");
//
//                rightLed.off();
//                leftLed.on(RGBLed.COLOR.RED);
//            } else {
//                LOG.info("Right light");
//                leftLed.off();
//                rightLed.on(RGBLed.COLOR.RED);
//            }
//
//            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
//        }
//        LOG.info("We are done");
//        rightLed.off();
//        leftLed.off();



//        Compass c = board.getCompass(1);
//        c.test();

//        Read data [-2, 68, -2, -94, 3, -6]

//        byte[] buffer = {-2, 68, -2, -94, 3, -6};
//        int yval = ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);
//        int y = (buffer[4] << 8) + buffer[5];
//        LOG.info("Test Y {} Y2 {}", yval, y);

        LOG.info("We are done");

        System.exit(0);
    }

}
