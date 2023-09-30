package com.oberasoftware.trainautomation.rocoz21.responsehandlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.events.impl.ThingMultiValueEventImpl;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.robo.core.ConverterUtil;
import com.oberasoftware.trainautomation.api.SensorEvent;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LoconetHandler implements EventHandler {
    private static final Logger LOG = getLogger( LoconetHandler.class );

    @Autowired
    private LocalEventBus localEventBus;

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0xA4)
    public void receive(Z21ReturnPacket packet) {
        LOG.info("Received a loconet package: {}", packet);

        var d = packet.getData();
        int feedbackType = d[0];
        int port = ConverterUtil.byteToInt(d[1], d[2]);


        var o = BlockStatus.FREE;
        int locAddress = -1;
        switch(feedbackType) {
            case 0x01:
                o = d[3] == 0x01 ? BlockStatus.OCCUPIED : BlockStatus.FREE;
                break;
            case 0x02:
                o = BlockStatus.OCCUPIED;
                locAddress = ConverterUtil.byteToInt(d[3], d[4]);
                port = port + 1;
                break;
            case 0x03:
                o = BlockStatus.FREE;
                locAddress = ConverterUtil.byteToInt(d[3], d[4]);
                port = port + 1;
                break;
            default:
                LOG.warn("Feedback type not supported");
                break;
        }
        LOG.info("Received a feedback type: {} on Loconet port: {} state: {} for Loc: {}", String.format("%02X", feedbackType), port, o, locAddress);
        localEventBus.publish(new SensorEvent(port, "occupancy", new ValueImpl(VALUE_TYPE.STRING, o.toString().toLowerCase())));
        localEventBus.publish(new SensorEvent(port, "locomotive", new ValueImpl(VALUE_TYPE.NUMBER, locAddress)));


    }
}
