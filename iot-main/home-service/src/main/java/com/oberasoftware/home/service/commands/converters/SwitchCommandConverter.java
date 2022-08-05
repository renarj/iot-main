package com.oberasoftware.home.service.commands.converters;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.commands.converters.CommandConverter;
import com.oberasoftware.iot.core.commands.converters.ConverterType;
import com.oberasoftware.iot.core.commands.impl.SwitchCommandImpl;
import org.springframework.stereotype.Component;

/**
 * @author renarj
 */
@Component
public class SwitchCommandConverter implements CommandConverter<BasicCommand, SwitchCommand> {

    @Override
    @ConverterType(commandType = "switch")
    public SwitchCommand map(BasicCommand source) {

        String value = source.getProperties().get("value");
        String itemId = source.getItemId();
        String controllerId = source.getControllerId();

        switch(value.toLowerCase()) {
            case "on":
                return new SwitchCommandImpl(source.getControllerId(), itemId, SwitchCommand.STATE.ON);
            case "off":
            default:
                return new SwitchCommandImpl(source.getControllerId(), itemId, SwitchCommand.STATE.OFF);
        }

    }
}
