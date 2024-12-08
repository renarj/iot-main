package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.general.ActivateMotion;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.springframework.stereotype.Component;

@Component
public class MotionParser implements BlockParser<ActivateMotion> {
    @Override
    public boolean supportsType(String type) {
        return "activateMotion".equalsIgnoreCase(type);
    }

    @Override
    public ActivateMotion transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        BlockUtils.assertInputPresent(block, "motion", "No Motion was specified in block");
        var motionBlock = BlockUtils.safeGetInput(block, "motion");
        var controllerId = BlockUtils.getControllerId(motionBlock.getType());
        var motionId = BlockUtils.getThingId(motionBlock.getType());

        return new ActivateMotion(controllerId, motionId);
    }
}
