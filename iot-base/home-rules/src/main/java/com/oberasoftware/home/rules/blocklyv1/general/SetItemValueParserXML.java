package com.oberasoftware.home.rules.blocklyv1.general;

import com.oberasoftware.home.rules.api.general.SetState;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blocklyv1.BlockParserFactory;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blocklyv1.values.ItemValueParserXML;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findElementWithAttribute;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFirstBlock;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class SetItemValueParserXML implements XMLBlockParser<SetState> {
    private static final Logger LOG = getLogger(SetItemValueParserXML.class);

    @Autowired
    private BlockParserFactory blockParserFactory;

    @Override
    public boolean supportsType(String type) {
        return "setItemValue".equals(type);
    }

    @Override
    public SetState parse(Element node) throws BlocklyParseException {
        Element labelValue = findElementWithAttribute(node, "value", "name", "label")
                .orElseThrow(() -> new BlocklyParseException("No label specified in device value"));
        Element labelBlock = findFirstBlock(labelValue)
                .orElseThrow(() -> new BlocklyParseException("No label block defined"));
        String type = labelBlock.getAttribute("type");
        XMLBlockParser<ResolvableValue> blockParser = blockParserFactory.getParser(type);
        String label = ItemValueParserXML.getLabelValue(blockParser.parse(labelBlock));
        LOG.debug("Found device label criteria: {}", label);

        Element itemElement = findElementWithAttribute(node, "value", "name", "item")
                .orElseThrow(() -> new BlocklyParseException("No item specified"));
        Element itemBlock = findFirstBlock(itemElement)
                .orElseThrow(() -> new BlocklyParseException("No item specified"));
        String itemDescriptor = itemBlock.getAttribute("type");
        String controllerId = itemDescriptor.substring(0, itemDescriptor.indexOf("."));
        String thingId = itemDescriptor.substring(itemDescriptor.indexOf(".") + 1);
        LOG.debug("Found itemId: {}/{}", controllerId, thingId);


        Element valueElement = findElementWithAttribute(node, "value", "name", "value")
                .orElseThrow(() -> new BlocklyParseException("No value specified for setting"));
        Element valueBlock = findFirstBlock(valueElement)
                .orElseThrow(() -> new BlocklyParseException("No value block set"));
        String valueType = valueBlock.getAttribute("type");
        XMLBlockParser<ResolvableValue> valueBlockParser = blockParserFactory.getParser(valueType);
        ResolvableValue value = valueBlockParser.parse(valueBlock);

        return new SetState(new ThingAttributeValue(controllerId, thingId, label), value);
    }
}
