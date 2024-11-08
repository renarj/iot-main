package com.oberasoftware.home.rules.blockly.blocks.triggers;

import com.oberasoftware.home.rules.api.trigger.ThingTrigger;
import com.oberasoftware.home.rules.api.trigger.Trigger;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class ThingTriggerParser implements BlockParser<Trigger> {

    @Override
    public boolean supportsType(String type) {
        return "thingTrigger".equals(type);
    }

    @Override
    public Trigger transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        return new ThingTrigger(ThingTrigger.TRIGGER_TYPE.THING_STATE_CHANGE);
    }
}
