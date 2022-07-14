package com.oberasoftware.trainautomation.rocoz21.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LanStatusHandler implements EventHandler {
    private static final Logger LOG = getLogger( LanStatusHandler.class );

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0x62)
    public void receiveLanStatus(Z21ReturnPacket returnPacket) {
        LOG.info("Received a LAN Status package from the Z21: {}", returnPacket);
        byte[] data = returnPacket.getData();
        int status = data[2];

        boolean emergencyStop = (status & 0x01) != 0;
        boolean trackVoltageOff = (status & 0x02) != 0;
        boolean shortCircuit = (status & 0x04) != 0;
        boolean programMode = (status & 0x20) != 0;

        LOG.info("Emergency stop: {} track voltage off: {} short Circuit: {} program Mode: {}", emergencyStop, trackVoltageOff, shortCircuit, programMode);
    }

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0x61, firstDataByte = 0x01)
    public void receiveTrackOn(Z21ReturnPacket packet) {
        LOG.info("Received message that track voltage is switched ON");
    }

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0x61, firstDataByte = 0x00)
    public void receiveTrackOff(Z21ReturnPacket packet) {
        LOG.info("Received message that track voltage is switched OFF");
    }

}
