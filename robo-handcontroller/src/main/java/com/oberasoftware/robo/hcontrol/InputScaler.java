package com.oberasoftware.robo.hcontrol;

import java.math.BigDecimal;
import java.math.RoundingMode;

class InputScaler {
    private static final double MAX_SCALE = 100.0;

    static int convert(Double val) {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        double roundedValue = bd.doubleValue();

        if(roundedValue == 2.5) {
            return 0;
        } else if(roundedValue < 2.5 && roundedValue >= 1.3) {
            double dist = (2.6 - roundedValue) * 10.0;

            double range = (2.6 - 1.3) * 10.0;
            double factor = MAX_SCALE / range;

            return -((int)(dist * factor));
        } else {
            double dist = (roundedValue - 2.6) * 10.0;

            double range = (3.9 - 2.6) * 10.0;
            double factor = MAX_SCALE / range;

            return ((int)(dist * factor));
        }
    }

}
