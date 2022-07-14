package com.oberasoftware.trainautomation.rocoz21.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Z21PackageHandler implements EventHandler {
    private static final Logger LOG = getLogger( Z21PackageHandler.class );

    @EventSubscribe
    public void receive(Z21ReturnPacket returnPacket) {
        LOG.info("Received a return package from the Z21: {}", returnPacket);
    }



}
