package com.oberasoftware.home.rules.blocklyv1.trigger;

import com.oberasoftware.home.rules.api.trigger.DayTimeTrigger;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blocklyv1.XMLUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

/**
 * @author Renze de Vries
 */
@Component
public class DayTimeTriggerParserXML implements XMLBlockParser<DayTimeTrigger> {
    @Override
    public boolean supportsType(String type) {
        return "dayTimeTrigger".equals(type);
    }

    @Override
    public DayTimeTrigger parse(Element node) throws BlocklyParseException {
        String hourText = XMLUtils.findFieldElement(node, "hour").get().getTextContent();
        String minuteText = XMLUtils.findFieldElement(node, "minute").get().getTextContent();

        try {
            int hour = Integer.parseInt(hourText);
            int minute = Integer.parseInt(minuteText);

            return new DayTimeTrigger(hour, minute);
        } catch(NumberFormatException e) {
            throw new BlocklyParseException("Unable to parse hour / minute from trigger");
        }
    }
}
