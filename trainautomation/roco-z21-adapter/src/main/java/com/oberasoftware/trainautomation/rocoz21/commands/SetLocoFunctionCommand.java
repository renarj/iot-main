package com.oberasoftware.trainautomation.rocoz21.commands;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.train.FunctionState;
import com.oberasoftware.trainautomation.TrainCommand;
import com.oberasoftware.trainautomation.rocoz21.Z21Connector;
import com.oberasoftware.trainautomation.rocoz21.commandhandlers.Z21CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SetLocoFunctionCommand implements Z21CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SetLocoFunctionCommand.class);

    private static final String FUNCTION = "function";
    private static final String FUNCTION_STATE = "FUNCTION_STATE";

    @Autowired
    private Z21Connector connector;

    @Override
    public boolean supportsCommand(TrainCommand command) {
        return command.getAttributes().containsKey(FUNCTION)
                && command.getAttributes().containsKey(FUNCTION_STATE);
    }

    @Override
    public void action(TrainCommand command) {
        var functionNr = Integer.parseInt(command.getAttribute(FUNCTION).asString());
        FunctionState state = FunctionState.valueOf(command.getAttribute(FUNCTION_STATE).asString());

        var z21Command = new LocoFunctionCommand(command.getLocAddress(), functionNr, state);
        LOG.info("Sending Loco function: {} to loc: {} with state: {}", functionNr, command.getLocAddress(), state);
        try {
            connector.send(z21Command);

            if(state == FunctionState.TOGGLE) {
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                z21Command = new LocoFunctionCommand(command.getLocAddress(), functionNr, FunctionState.OFF);
                connector.send(z21Command);
            }

        } catch (IOTException e) {
            LOG.error("Could not send Function command", e);
        }
    }

    public static class LocoFunctionCommand extends Z21Command {
        public LocoFunctionCommand(int locAddress, int function, FunctionState state) {
            addHeader(0x40);
            addXHeader(0xE4, 0xF8);
            addSingleByte(getHighAddress(locAddress));
            addSingleByte(getLowAddress(locAddress));

            int functionByte = function & 0x3F;
            switch(state) {
                case ON -> {
                    functionByte = functionByte | 0x40;
                }
                case TOGGLE -> {
                    functionByte = functionByte | 0x80;
                }
            }
            addSingleByte(functionByte);
        }
    }
}
