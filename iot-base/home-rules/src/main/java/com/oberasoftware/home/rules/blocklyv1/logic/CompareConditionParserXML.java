package com.oberasoftware.home.rules.blocklyv1.logic;

import com.oberasoftware.home.rules.api.Operator;
import com.oberasoftware.home.rules.api.logic.CompareCondition;
import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.blocklyv1.BaseValueParser;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import static com.oberasoftware.home.rules.api.Operator.EQUALS;
import static com.oberasoftware.home.rules.api.Operator.LARGER_THAN;
import static com.oberasoftware.home.rules.api.Operator.LARGER_THAN_EQUALS;
import static com.oberasoftware.home.rules.api.Operator.SMALLER_THAN;
import static com.oberasoftware.home.rules.api.Operator.SMALLER_THAN_EQUALS;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFieldElement;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class CompareConditionParserXML extends BaseValueParser implements XMLBlockParser<CompareCondition> {
    private static final Logger LOG = getLogger(CompareConditionParserXML.class);

    @Override
    public boolean supportsType(String type) {
        return type.equals("logic_compare");
    }

    @Override
    public CompareCondition parse(Element node) throws BlocklyParseException {
        String operatorText = findFieldElement(node, "OP")
                .orElseThrow(() -> new BlocklyParseException("No operator defined")).getTextContent();
        Operator operator = getOperator(operatorText);
        LOG.debug("Operator: {}", operator);

        ResolvableValue leftValue = getResolvableValue(node, "A");
        ResolvableValue rightValue = getResolvableValue(node, "B");

        return new CompareCondition(leftValue, operator, rightValue);
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
