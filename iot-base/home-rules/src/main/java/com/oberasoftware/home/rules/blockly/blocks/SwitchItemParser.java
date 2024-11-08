package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.general.SwitchItem;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.jasdb.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SwitchItemParser implements BlockParser<SwitchItem> {
    private static final Logger LOG = LoggerFactory.getLogger(SwitchItemParser.class);

    @Override
    public boolean supportsType(String type) {
        return "switch_item".equalsIgnoreCase(type);
    }

    @Override
    public SwitchItem transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var itemBlock = BlockUtils.safeGetInput(block, "item");
        var itemDescriptor = itemBlock.getType();

        String thingId = BlockUtils.getThingId(itemDescriptor);
        String controllerId = BlockUtils.getControllerId(itemDescriptor);
        var attributeBlock = BlockUtils.safeGetInput(block, "Attribute");
        var attribute = BlockUtils.safeGetField(attributeBlock, "label");

        String stateText = BlockUtils.safeGetField(block, "state");
        SwitchCommand.STATE targetState = SwitchCommand.STATE.OFF;
        if(StringUtils.stringNotEmpty(stateText) && stateText.equalsIgnoreCase("on")) {
            targetState = SwitchCommand.STATE.ON;
        }
        LOG.debug("Target state: {} for item: {} and attribute: {}", targetState, itemDescriptor, attribute);

        return new SwitchItem(controllerId, thingId, attribute, targetState);
    }
}
