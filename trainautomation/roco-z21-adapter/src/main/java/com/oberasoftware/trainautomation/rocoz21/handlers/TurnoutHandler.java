package com.oberasoftware.trainautomation.rocoz21.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.train.SwitchState;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class TurnoutHandler implements EventHandler {
    private static final Logger LOG = getLogger( TurnoutHandler.class );

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0x43)
    public void receive(Z21ReturnPacket packet) {
        LOG.info("Received a turnout info package: {}", packet);

        var data = packet.getData();

        int address = (data[1] << 8 ) + data[2] +1;
        var state = switch(data[3]) {
            case 0x01:
                yield SwitchState.POS_P1;
            case 0x02:
                yield SwitchState.POS_P0;
            default:
                yield SwitchState.UNKNOWN;
        };

        LOG.info("Turnout: {} is in state: {}", address, state);
    }
}
