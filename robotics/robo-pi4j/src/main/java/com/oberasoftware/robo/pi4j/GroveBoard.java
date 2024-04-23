package com.oberasoftware.robo.pi4j;

import com.oberasoftware.iot.core.robotics.ActivatableCapability;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
public class GroveBoard implements ActivatableCapability {
    private static final Logger LOG = getLogger(GroveBoard.class);

    private static final byte ADDRESS = 0x04;

    private I2CDevice mainPort;

    private I2CDevice port1;

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        try {
            int busId = I2CBus.BUS_1;
            String type = SystemInfo.getBoardType().name();
            if (type.indexOf("ModelA") > 0) {
                busId = I2CBus.BUS_0;
            }

            I2CBus bus = I2CFactory.getInstance(busId);
            mainPort = bus.getDevice(ADDRESS);

//            port1 = bus.getDevice(0x1E);
        } catch(Exception e) {
            LOG.error("Could not initialize Grove board");
        }
    }

    public RGBLed getLed(int pin) {
        return new RGBLed(new GrovePort(mainPort), pin);
    }

    public Compass getCompass(int i2cPort) {
        if(i2cPort == 1) {
            return new Compass(new GrovePort(port1));
        } else {
            throw new IllegalArgumentException("No such port");
        }
    }

    public ThumbStick getThumbStick() {
        return new ThumbStick(new GrovePort(mainPort));
    }

    @Override
    public void shutdown() {

    }

}
