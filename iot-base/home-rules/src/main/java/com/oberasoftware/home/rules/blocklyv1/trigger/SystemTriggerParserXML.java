package com.oberasoftware.home.rules.blocklyv1.trigger;

import com.oberasoftware.home.rules.api.trigger.SystemTrigger;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

/**
 * @author Renze de Vries
 */
@Component
public class SystemTriggerParserXML implements XMLBlockParser<SystemTrigger> {
    @Override
    public boolean supportsType(String type) {
        return "systemTrigger".equals(type);
    }

    @Override
    public SystemTrigger parse(Element node) throws BlocklyParseException {
        return new SystemTrigger();
    }
}
