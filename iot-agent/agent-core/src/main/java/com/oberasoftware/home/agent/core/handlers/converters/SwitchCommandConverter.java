package com.oberasoftware.home.agent.core.handlers.converters;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.commands.impl.SwitchCommandImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class SwitchCommandConverter implements CommandConverter<BasicCommand, SwitchCommand> {
    private static final Logger LOG = getLogger( SwitchCommandConverter.class );

    @Override
    @ConverterType(commandType = "switch")
    public SwitchCommand map(BasicCommand source) {

        if(source.getProperties().containsKey("value")) {
            String value = source.getProperties().get("value");
            String itemId = source.getThingId();
            String controllerId = source.getControllerId();

            switch (value.toLowerCase()) {
                case "on":
                    return new SwitchCommandImpl(controllerId, itemId, SwitchCommand.STATE.ON);
                case "off":
                default:
                    return new SwitchCommandImpl(controllerId, itemId, SwitchCommand.STATE.OFF);
            }
        } else {
            LOG.warn("Could not map basic command: {}, missing target value for switch state", source);
            return null;
        }
    }
}
