package com.oberasoftware.robo.hcontrol;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.slf4j.LoggerFactory.getLogger;

public class InputScaler {
    private static final Logger LOG = getLogger(InputScaler.class);

    private static final double MAX_SCALE = 100.0;
    private static final double MAX_VOLTAGE = 4.0;
    private static final double HALF_SCALE = 50.0;
    private static final double SCALE_FACTOR = 2.0;


    public static final int convert(Double val) {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(1, RoundingMode.HALF_UP);

        LOG.info("V: {}", bd.doubleValue());

        double roundedValue = bd.doubleValue();

        if(roundedValue == 2.5) {
            return 0;
        } else if(roundedValue < 2.5 && roundedValue >= 1.3) {
            double dist = (2.6 - roundedValue) * 10.0;

            double range = (2.6 - 1.3) * 10.0;
            double factor = 100.0 / range;



            return -((int)(dist * factor));
        } else {
            double dist = (roundedValue - 2.6) * 10.0;

            double range = (3.9 - 2.6) * 10.0;
            double factor = 100.0 / range;

            return ((int)(dist * factor));
        }
//
//        double factor = MAX_SCALE / MAX_VOLTAGE;
//
//        Double c = val * factor;
//
//        if(c < HALF_SCALE) {
//            return (int)(-(HALF_SCALE - c) * SCALE_FACTOR);
//        } else {
//            return (int)((c - HALF_SCALE) * SCALE_FACTOR);
//        }
    }

}
