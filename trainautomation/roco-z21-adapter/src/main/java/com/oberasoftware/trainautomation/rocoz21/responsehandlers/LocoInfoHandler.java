package com.oberasoftware.trainautomation.rocoz21.responsehandlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.train.DirectionEnum;
import com.oberasoftware.iot.core.train.StepMode;
import com.oberasoftware.iot.core.train.model.Locomotive;
import com.oberasoftware.trainautomation.api.LocEvent;
import com.oberasoftware.trainautomation.rocoz21.Z21ResponseFilter;
import com.oberasoftware.trainautomation.rocoz21.Z21ReturnPacket;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LocoInfoHandler implements EventHandler {
    private static final Logger LOG = getLogger( LocoInfoHandler.class );

    @Autowired
    private LocalEventBus localEventBus;

    private static final List<FunctionParser> functionParsers = new ArrayList<>();
    static {
        functionParsers.add(new FunctionParser("0", bytes -> bytes[5], 0x10)); //F0
        functionParsers.add(new FunctionParser("1", bytes -> bytes[5], 0x01)); //F1
        functionParsers.add(new FunctionParser("2", bytes -> bytes[5], 0x02)); //F2
        functionParsers.add(new FunctionParser("3", bytes -> bytes[5], 0x04)); //F3
        functionParsers.add(new FunctionParser("4", bytes -> bytes[5], 0x08)); //F4
        functionParsers.add(new FunctionParser("5", bytes -> bytes[6], 0x01)); //F5
        functionParsers.add(new FunctionParser("6", bytes -> bytes[6], 0x02)); //F6
        functionParsers.add(new FunctionParser("7", bytes -> bytes[6], 0x04)); //F7
        functionParsers.add(new FunctionParser("8", bytes -> bytes[6], 0x08)); //F8
        functionParsers.add(new FunctionParser("9", bytes -> bytes[6], 0x10)); //F9
        functionParsers.add(new FunctionParser("10", bytes -> bytes[6], 0x20)); //F10
        functionParsers.add(new FunctionParser("11", bytes -> bytes[6], 0x40)); //F11
        functionParsers.add(new FunctionParser("12", bytes -> bytes[6], 0x80)); //F12
    }

    @EventSubscribe
    @Z21ResponseFilter(packageHeader = 0x40, xHeader = 0xEF)
    public void receive(Z21ReturnPacket packet) {
        LOG.info("Received a Loc Info package: {}", packet);
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
        localEventBus.publish(new LocEvent(locAddr, Locomotive.SPEED_ATTR, new ValueImpl(VALUE_TYPE.NUMBER, speed)));
        localEventBus.publish(new LocEvent(locAddr, Locomotive.DIRECTION_ATTR, new ValueImpl(VALUE_TYPE.STRING, direction.name())));

        functionParsers.forEach(p -> {
            var state = p.getState(data);
            var name = p.getFunctionName();

            localEventBus.publish(new LocEvent(locAddr, name, new ValueImpl(VALUE_TYPE.BOOLEAN, state)));
        });
    }

    private boolean validate(byte b, int condition) {
        return (b & condition) == condition;
    }

    private static final class FunctionParser {
        private final Function<byte[], Byte> byteSelector;
        private final int bitMask;

        private final String functionName;

        private FunctionParser(String functionName, Function<byte[], Byte> byteSelector, int bitMask) {
            this.functionName = functionName;
            this.byteSelector = byteSelector;
            this.bitMask = bitMask;
        }

        public boolean getState(byte[] data) {
            byte result = byteSelector.apply(data);
            return (result & bitMask) != 0;
        }

        public String getFunctionName() {
            return functionName;
        }
    }
}
