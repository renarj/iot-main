package com.oberasoftware.home.agent.core.handlers.converters;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.commands.impl.SwitchCommandImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class SwitchCommandConverter implements CommandConverter<BasicCommand, SwitchCommand> {
    private static final Logger LOG = getLogger( SwitchCommandConverter.class );

    @Override
    @ConverterType(commandType = CommandType.SWITCH)
    public SwitchCommand map(BasicCommand source) {

        if(!source.getAttributes().isEmpty()) {
            Map<String, SwitchCommand.STATE> states = new HashMap<>();
            String thingId = source.getThingId();
            String controllerId = source.getControllerId();

            source.getAttributes().forEach((k, v) -> {
                switch (v.toLowerCase()) {
                    case "on":
                        states.put(k, SwitchCommand.STATE.ON);
                        break;
                    case "off":
                    default:
                        states.put(k, SwitchCommand.STATE.OFF);
                        break;
                }
            });

            return new SwitchCommandImpl(controllerId, thingId, states);
        } else {
            LOG.warn("Could not map basic command: {}, missing target value for switch state", source);
            return null;
        }
    }
}
