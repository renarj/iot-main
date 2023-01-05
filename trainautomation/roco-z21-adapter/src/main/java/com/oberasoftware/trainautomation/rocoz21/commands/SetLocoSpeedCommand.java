package com.oberasoftware.trainautomation.rocoz21.commands;

import com.oberasoftware.iot.core.train.DirectionEnum;
import com.oberasoftware.iot.core.train.StepMode;

public class SetLocoSpeedCommand extends Z21Command {
    public SetLocoSpeedCommand(int address, StepMode mode, int speed, DirectionEnum direction) {
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
