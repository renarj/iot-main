package com.oberasoftware.home.rules.blockly.blocks.triggers;

import com.oberasoftware.home.rules.api.trigger.DayTimeTrigger;
import com.oberasoftware.home.rules.blockly.*;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class DayTimeTriggerParser implements BlockParser<DayTimeTrigger> {
    private static final Logger LOG = LoggerFactory.getLogger(DayTimeTriggerParser.class);

    @Override
    public boolean supportsType(String type) {
        return "dayTimeTrigger".equals(type);
    }

    @Override
    public DayTimeTrigger transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        LOG.info("Evaluating DayTimeTrigger block: {}", block);
        String hourText = BlockUtils.safeGetField(block, "hour");
        String minuteText = BlockUtils.safeGetField(block, "minute");

        try {
            int hour = Integer.parseInt(hourText);
            int minute = Integer.parseInt(minuteText);

            return new DayTimeTrigger(hour, minute);
        } catch(NumberFormatException e) {
            throw new BlocklyParseException("Unable to parse hour / minute from trigger");
        }
    }
}
