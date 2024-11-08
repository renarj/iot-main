package com.oberasoftware.home.rules.blockly.blocks.logic;

import com.oberasoftware.home.rules.api.Operator;
import com.oberasoftware.home.rules.api.logic.CompareCondition;
import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.springframework.stereotype.Component;

import static com.oberasoftware.home.rules.api.Operator.*;

@Component
public class LogicCompareParser implements BlockParser<CompareCondition> {
    @Override
    public boolean supportsType(String type) {
        return "logic_compare".equalsIgnoreCase(type);
    }

    @Override
    public CompareCondition transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var operatorAsText = BlockUtils.safeGetField(block, "OP");
        var operator = getOperator(operatorAsText);
        var leftCondition = getCondition(factory, block, "A");
        var rightCondition = getCondition(factory, block, "B");

        return new CompareCondition(leftCondition, operator, rightCondition);
    }

    private ResolvableValue getCondition(BlockFactory factory, BlocklyObject block, String input) {
        var blockCondition = BlockUtils.safeGetInput(block, input);
        var parser = factory.getParser(blockCondition.getType());
        return (ResolvableValue) parser.transform(factory, blockCondition);
    }

    private Operator getOperator(String operator) {
        switch(operator) {
            case "LTE":
                return SMALLER_THAN_EQUALS;
            case "LT":
                return SMALLER_THAN;
            case "GTE":
                return LARGER_THAN_EQUALS;
            case "GT":
                return LARGER_THAN;
            case "EQ":
            default:
                return EQUALS;
        }
    }
}
