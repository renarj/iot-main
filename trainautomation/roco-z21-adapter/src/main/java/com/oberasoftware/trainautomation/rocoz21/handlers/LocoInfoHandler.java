package com.oberasoftware.trainautomation.rocoz21.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.train.DirectionEnum;
import com.oberasoftware.iot.core.train.StepMode;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LocoInfoHandler implements EventHandler {
    private static final Logger LOG = getLogger( LocoInfoHandler.class );

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0xEF)
    public void receive(Z21ReturnPacket packet) {
        LOG.info("Received a Loc Info package: {}", packet);

//Z21ReturnPacket{data=EF 00 1B 04 80 30 00 00 00 00 00 00 00 00 00 00 40, responseHeader=40, length=21, xHeader=EF}
//        int messageaddress = ((getElement(1) & 0x3F) << 8) + (getElement(2) & 0xff);
        var data = packet.getData();

        int locAddr = ((data[1] & 0x3F) << 8) + (data[2] & 0xff);

        var direction = (data[4] & 0x80) == 0x80 ? DirectionEnum.FORWARD : DirectionEnum.REVERSE;

        var stepMode = StepMode.DCC_14;
        var speed = data[4] & 0x7f;
        if(validate(data[3], 0x04)) {
            stepMode = StepMode.DCC_128;
        } else if(validate(data[3], 0x02)) {
            stepMode = StepMode.DCC_28;
            speed = ((data[4] & 0x0F) << 1) + ((data[4] & 0x10) >> 4);
        } else if(validate(data[3], 0x01)) {
            stepMode = StepMode.DCC_27;
            speed = ((data[4] & 0x0F) << 1) + ((data[4] & 0x10) >> 4);
        } else {
            speed = (data[4] & 0x0F);
        }

        LOG.info("Locomotive: {} has direction: {} with stepMode: {} and speed: {}", locAddr, direction, stepMode, speed);
    }

    private boolean validate(byte b, int condition) {
        return (b & condition) == condition;
    }
}
