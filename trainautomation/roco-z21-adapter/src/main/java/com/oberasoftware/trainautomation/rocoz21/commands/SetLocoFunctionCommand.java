package com.oberasoftware.trainautomation.rocoz21.commands;

import com.oberasoftware.iot.core.train.FunctionState;

public class SetLocoFunctionCommand extends Z21Command {
    public SetLocoFunctionCommand(int locAddress, int function, FunctionState state) {
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
