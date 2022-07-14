package com.oberasoftware.trainautomation.rocoz21.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.core.ConverterUtil;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class CanDetectorHandler implements EventHandler {
    private static final Logger LOG = getLogger( CanDetectorHandler.class );

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0xC4)
    public void receive(Z21ReturnPacket packet) {
        LOG.debug("Received a CAN Detector occupancy package: {}", packet);
        var d = packet.getData();
        var moduleAddress = ConverterUtil.byteToInt(d[2], d[3]);
        var port = (int)d[4];
        var occupancyState = (int)d[5];

        if(occupancyState >= 0x11) {
            detectLocomotive(d[6], d[7], moduleAddress, port);
            detectLocomotive(d[8], d[9], moduleAddress, port);
        } else {
            LOG.info("Occupancy detected on module: {} and port: {} no address read", moduleAddress, port);
        }
    }

    private void detectLocomotive(byte firstByte, byte secondByte, int module, int port) {
        int value = (firstByte & 0xFF) + ((secondByte & 0xFF) << 8);
        int direction = (0xC000 & value);
        int locAddress = (0x3FFF & value);
        LOG.info("Detected a locomotive: {} with direction: {} on module: {} and port: {}", locAddress, direction, module, port);
    }
}
