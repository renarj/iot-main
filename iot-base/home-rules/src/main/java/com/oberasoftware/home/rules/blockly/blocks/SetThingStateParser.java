package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.general.SetState;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetThingStateParser implements BlockParser<SetState> {
    private static final Logger LOG = LoggerFactory.getLogger(SetThingStateParser.class);

    @Override
    public boolean supportsType(String type) {
        return "setThingValue".equalsIgnoreCase(type);
    }

    @Override
    public SetState transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var thingBlock = BlockUtils.safeGetInput(block, "item");
        var attributeBlock = BlockUtils.safeGetInput(block, "Attribute");
        var attribute = "";
        if("attribute_text".equalsIgnoreCase(attributeBlock.getType())) {
            attribute = BlockUtils.safeGetField(attributeBlock, "attribute");
        } else {
            attribute = BlockUtils.safeGetField(attributeBlock, "label");
        }

        var valueBlock = BlockUtils.safeGetInput(block, "value");

        LOG.info("Thing: {} with attribute: {} and value: {}", thingBlock, attribute, valueBlock);

        return null;
    }
}
