package com.oberasoftware.home.rules.blocklyv1.values;

import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.api.values.StaticValue;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFieldElement;

/**
 * @author Renze de Vries
 */
@Component
public class LabelXMLBlockParser implements XMLBlockParser<ResolvableValue> {
    @Override
    public boolean supportsType(String type) {
        return "label".equals(type) || "label_text".equalsIgnoreCase(type);
    }

    @Override
    public ResolvableValue parse(Element node) throws BlocklyParseException {
        Element labelElement = findFieldElement(node, "label")
                .orElseThrow(() -> new BlocklyParseException("Could not find label element"));

        String labelText = labelElement.getTextContent();
        if(labelText.equals("movement")) {
            //we do this as movement is not an official label in haas
            labelText = "on-off";
        }

        return new StaticValue(labelText, VALUE_TYPE.STRING);
    }
}
