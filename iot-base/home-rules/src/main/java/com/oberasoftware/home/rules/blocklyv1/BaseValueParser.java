package com.oberasoftware.home.rules.blocklyv1;

import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findElementWithAttribute;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFirstBlock;

/**
 * @author Renze de Vries
 */
public class BaseValueParser {

    @Autowired
    private BlockParserFactory blockParserFactory;

    protected ResolvableValue getResolvableValue(Element node, String paramName) throws BlocklyParseException {
        Element value = findElementWithAttribute(node, "value", "name", paramName)
                .orElseThrow(() -> new BlocklyParseException("No value specified in compare condition"));
        Element valueBlock = findFirstBlock(value)
                .orElseThrow(() -> new BlocklyParseException("No condition specified in side of compare"));

        String blockType = valueBlock.getAttribute("type");
        XMLBlockParser<ResolvableValue> valueBlockParser = blockParserFactory.getParser(blockType);
        return valueBlockParser.parse(valueBlock);
    }
}
