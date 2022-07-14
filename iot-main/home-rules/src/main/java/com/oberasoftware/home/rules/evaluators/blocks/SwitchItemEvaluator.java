package com.oberasoftware.home.rules.evaluators.blocks;

import com.oberasoftware.home.api.AutomationBus;
import com.oberasoftware.home.rules.api.general.SwitchItem;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.iot.core.commands.impl.SwitchCommandImpl;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class SwitchItemEvaluator implements BlockEvaluator<SwitchItem> {
    private static final Logger LOG = getLogger(SwitchItemEvaluator.class);

    @Autowired
    private AutomationBus automationBus;

    @Override
    public Boolean eval(SwitchItem input) {
        LOG.debug("Generating a switch command: {}", input);

        ItemCommand command = new SwitchCommandImpl(input.getItemId(), input.getState());

        automationBus.publish(new ItemCommandEvent(input.getItemId(), command));

        return true;
    }
}
