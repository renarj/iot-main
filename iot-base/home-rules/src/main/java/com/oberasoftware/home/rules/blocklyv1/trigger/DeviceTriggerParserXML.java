package com.oberasoftware.home.rules.blocklyv1.trigger;

import com.oberasoftware.home.rules.api.trigger.ThingTrigger;
import com.oberasoftware.home.rules.api.trigger.Trigger;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

/**
 * @author Renze de Vries
 */
@Component
public class DeviceTriggerParserXML implements XMLBlockParser<Trigger> {

    @Override
    public boolean supportsType(String type) {
        return "deviceTrigger".equals(type);
    }

    @Override
    public Trigger parse(Element node) throws BlocklyParseException {
        return new ThingTrigger(ThingTrigger.TRIGGER_TYPE.THING_STATE_CHANGE);
    }
}
