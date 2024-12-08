package com.oberasoftware.home.rules.evaluators.blocks;

import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.rules.api.general.ActivateMotion;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.commands.ThingCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActivateMotionEvaluator implements BlockEvaluator<ActivateMotion> {
    private static final Logger LOG = LoggerFactory.getLogger(ActivateMotionEvaluator.class);

    @Autowired
    private LocalEventBus automationBus;

    @Autowired
    private AgentClient agentClient;

    @Override
    public Boolean eval(ActivateMotion input) {
        var controller = input.getControllerId();
        var motion = input.getMotionId();

        try {
            var oThing = agentClient.getThing(controller, motion);
            if(oThing.isPresent()) {
                var robotId = oThing.get().getParentId();

                Map<String, Value> values = new HashMap<>();
                values.put("motion", new ValueImpl(VALUE_TYPE.STRING, motion));
                ThingCommand thingCommand = new ValueCommandImpl(controller, robotId, values);

                automationBus.publish(new ItemCommandEvent(robotId, thingCommand));
                return true;
            }
        } catch (IOTException e) {
            LOG.error("Could not get thing: {} on controller: {}", motion, controller);
        }

        return false;
    }
}
