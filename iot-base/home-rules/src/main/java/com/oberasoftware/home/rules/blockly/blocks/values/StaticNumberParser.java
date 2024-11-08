package com.oberasoftware.home.rules.blockly.blocks.values;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
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
public class StaticNumberParser implements BlockParser<ResolvableValue> {
    @Override
    public boolean supportsType(String type) {
        return "math_number".equalsIgnoreCase(type);
    }

    @Override
    public ResolvableValue transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var numberAsText = BlockUtils.safeGetField(block, "NUM");

        return new StaticValue(getNumber(numberAsText), VALUE_TYPE.NUMBER);
    }

    private Object getNumber(String text) {
        Long result = Longs.tryParse(text);
        if(result == null) {
            return Doubles.tryParse(text);
        } else {
            return result;
        }
    }

}
