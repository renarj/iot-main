package com.oberasoftware.home.rules.blocklyv1.logic;

import com.google.common.collect.Lists;
import com.oberasoftware.home.rules.api.Condition;
import com.oberasoftware.home.rules.api.logic.LogicCondition;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blocklyv1.BlockParserFactory;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blocklyv1.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

/**
 * @author Renze de Vries
 */
@Component
public class LogicParserXML implements XMLBlockParser<LogicCondition> {

    @Autowired
    private BlockParserFactory blockParserFactory;

    @Override
    public boolean supportsType(String type) {
        return "logic_operation".equalsIgnoreCase(type);
    }

    @Override
    public LogicCondition parse(Element node) throws BlocklyParseException {
        Element operatorField = XMLUtils.findFieldElement(node, "OP")
                .orElseThrow(() -> new BlocklyParseException("No Operator specified"));
        Condition leftCondition = getCondition(node, "A");
        Condition rightCondition = getCondition(node, "B");

        LogicCondition.TYPE operator = LogicCondition.TYPE.AND;
        if(operatorField.getTextContent().equalsIgnoreCase("OR")) {
            operator = LogicCondition.TYPE.OR;
        }

        return new LogicCondition(operator, Lists.newArrayList(leftCondition, rightCondition));
    }

    private Condition getCondition(Element parent, String fieldName) throws BlocklyParseException {
        Element conditionField = XMLUtils.findElementWithAttribute(parent, "value", "name", fieldName)
                .orElseThrow(() -> new BlocklyParseException("Unable to find condition: " + fieldName));
        Element conditionBlock = XMLUtils.findFirstBlock(conditionField)
                .orElseThrow(() -> new BlocklyParseException("No condition specified in Operation"));
        String blockType = conditionBlock.getAttribute("type");

        XMLBlockParser<Condition> conditionBlockParser = blockParserFactory.getParser(blockType);
        return conditionBlockParser.parse(conditionBlock);
    }
}
