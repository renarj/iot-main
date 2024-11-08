package com.oberasoftware.home.rules.blockly.blocks.values;

import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetThingParser implements BlockParser<ResolvableValue> {
    private static final Logger LOG = LoggerFactory.getLogger(GetThingParser.class);

    @Override
    public boolean supportsType(String type) {
        return "getThingValue".equalsIgnoreCase(type);
    }

    @Override
    public ResolvableValue transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var thingBlock = BlockUtils.safeGetInput(block, "item");
        var attributeBlock = BlockUtils.safeGetInput(block, "Attribute");

        String itemDescriptor = thingBlock.getType();
        String controllerId = itemDescriptor.substring(0, itemDescriptor.indexOf("."));
        String thingId = BlockUtils.getThingId(itemDescriptor);
        String attribute = BlockUtils.getControllerId(itemDescriptor);

        LOG.info("Found a Thing block with Controller/Thing/Attribute: {}/{}/{}", controllerId, thingId, attribute);

        return new ThingAttributeValue(controllerId, thingId, attribute);
    }
}
