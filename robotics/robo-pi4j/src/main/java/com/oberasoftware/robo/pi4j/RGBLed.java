package com.oberasoftware.robo.pi4j;

import static com.oberasoftware.robo.pi4j.GroveCodes.*;

/**
 * @author renarj
 */
public class RGBLed {

    private static final int NR_LEDS = 1;
    private final GrovePort port;
    private final int pin;

    public enum COLOR {
        OFF(0),
        BLUE(1),
        GREEN(2),
        CYAN(3),
        RED(4),
        MAGENTA(5),
        YELLOW(6),
        WHITE(7);

        int value;

        COLOR(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public RGBLed(GrovePort port, int pin) {
        this.port = port;
        this.pin = pin;

        port.write(PIN_MODE, pin, OUTPUT_MODE, UNUSED);
        port.write(RGB_INIT, pin, NR_LEDS, UNUSED);
    }

    public void on() {
        on(COLOR.WHITE);
    }

    public void on(COLOR color) {
        set(color);
    }

    public void on(int r, int g, int b) {

        //store color = 90
        port.write(90, r, g, b);

        //set modulo == 94
        port.write(94, pin, 0, 1);
    }

    public void off() {
        set(COLOR.OFF);
    }

    private void set(COLOR color) {
        port.write(GroveCodes.RGB_CONTROL, pin, NR_LEDS, color.getValue());
    }


}
