package com.oberasoftware.home.rules.blockly.blocks.triggers;

import com.oberasoftware.home.rules.api.trigger.SystemTrigger;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class SystemTriggerParser implements BlockParser<SystemTrigger> {
    @Override
    public boolean supportsType(String type) {
        return "systemTrigger".equals(type);
    }

    @Override
    public SystemTrigger transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        return new SystemTrigger();
    }
}
