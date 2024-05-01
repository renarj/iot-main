package com.oberasoftware.home.agent.core.handlers.converters;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.ThingCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.commands.impl.ConfigUpdatedCommand;
import org.springframework.stereotype.Component;

@Component
public class ConfigCommandConverter implements CommandConverter<BasicCommand, ThingCommand> {
    @Override
    @ConverterType(commandType = CommandType.CONFIG_UPDATE)
    public ThingCommand map(BasicCommand source) {
        return new ConfigUpdatedCommand(source.getControllerId(), source.getThingId());
    }
}
