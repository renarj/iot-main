package com.oberasoftware.trainautomation.rocoz21.commands;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.train.DirectionEnum;
import com.oberasoftware.iot.core.train.StepMode;
import com.oberasoftware.trainautomation.TrainCommand;
import com.oberasoftware.trainautomation.rocoz21.Z21Connector;
import com.oberasoftware.trainautomation.rocoz21.commandhandlers.Z21CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LocoSpeedCommandHandler implements Z21CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LocoSpeedCommandHandler.class);
    private static final String SPEED = "speed";
    private static final String DIRECTION = "direction";

    @Autowired
    private Z21Connector connector;

    @Override
    public boolean supportsCommand(TrainCommand command) {
        var properties = command.getAttributes();

        return properties.containsKey(SPEED)
                && properties.containsKey(DIRECTION);
    }

    @Override
    public void action(TrainCommand command) {
        var direction = findDirection(command.getAttribute(DIRECTION).getValue());
        var speed = (Long)command.getAttribute(SPEED).getValue();

        var z21Command = new LocoSpeedCommand(command.getLocAddress(), command.getStepMode(), speed.intValue(), direction);
        LOG.info("Sending loco speed command to loc: {} stepMode: {} direction: {} speed: {}", command.getLocAddress(), command.getStepMode(), direction, speed);
        try {
            connector.send(z21Command);
        } catch (IOTException e) {
            LOG.error("Could not send loco speed command: " + z21Command, e);
        }
    }

    private DirectionEnum findDirection(String direction) {
        var dO = Arrays.stream(DirectionEnum.values())
                .filter(d -> d.name().equalsIgnoreCase(direction)).findFirst();

        return dO.orElse(DirectionEnum.FORWARD);
    }

    private static class LocoSpeedCommand extends Z21Command {
        private LocoSpeedCommand(int address, StepMode mode, int speed, DirectionEnum direction) {
            addHeader(0x40);
            addXHeader(0xE4, mode.getFormat());
            addSingleByte(getHighAddress(address));
            addSingleByte(getLowAddress(address));

            int speedValue = 0;
            switch(mode) {
                case DCC_128 -> {
                    if(speed > 0 && speed < 126) {
                        speedValue = speed;
                    }
                }
            }

            switch(direction) {
                case FORWARD -> {
                    speedValue += 128;
                }
            }
            addSingleByte(speedValue);
        }
    }
}
