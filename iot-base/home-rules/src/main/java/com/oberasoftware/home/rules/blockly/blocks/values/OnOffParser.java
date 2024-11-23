package com.oberasoftware.home.rules.blockly.blocks.values;

import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.api.values.StaticValue;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import org.springframework.stereotype.Component;

@Component
public class OnOffParser implements BlockParser<ResolvableValue> {
    @Override
    public boolean supportsType(String type) {
        return "onoff".equalsIgnoreCase(type);
    }

    @Override
    public ResolvableValue transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var state = BlockUtils.safeGetField(block, "state");
        if(state.equalsIgnoreCase("on")) {
            return new StaticValue("on", VALUE_TYPE.STRING);
        } else {
            return new StaticValue("off", VALUE_TYPE.STRING);
        }
    }
}
