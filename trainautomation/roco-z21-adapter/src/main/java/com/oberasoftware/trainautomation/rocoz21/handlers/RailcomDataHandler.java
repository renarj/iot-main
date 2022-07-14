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
public class RailcomDataHandler implements EventHandler {
    private static final Logger LOG = getLogger( RailcomDataHandler.class );

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x88)
    public void receive(Z21ReturnPacket packet) {
        LOG.info("Received railcom data: {}", packet);

        byte[] data = packet.getData();
        int locNumber = ConverterUtil.byteToInt(data[0], data[1]);
        LOG.info("Detected Loc: {}", locNumber);
    }
}
