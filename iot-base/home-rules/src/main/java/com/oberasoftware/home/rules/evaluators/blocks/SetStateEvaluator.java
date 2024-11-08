package com.oberasoftware.home.rules.evaluators.blocks;

import com.google.common.collect.Sets;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.rules.api.general.SetState;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.evaluators.EvaluatorFactory;
import com.oberasoftware.home.rules.evaluators.values.ValueEvaluator;
import com.oberasoftware.iot.core.commands.ThingCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import com.oberasoftware.iot.core.model.states.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Renze de Vries
 */
@Component
public class SetStateEvaluator implements BlockEvaluator<SetState> {

    @Autowired
    private LocalEventBus automationBus;

    @Autowired
    private EvaluatorFactory evaluatorFactory;


    @Override
    public Boolean eval(SetState input) {
        ThingAttributeValue targetItem = input.getItemValue();

        ValueEvaluator<ResolvableValue> valueEvaluator = evaluatorFactory.getEvaluator(input.getResolvableValue());
        Map<String, Value> values = new HashMap<>();
        values.put(targetItem.getAttribute(), valueEvaluator.eval(input.getResolvableValue()));

        ThingCommand thingCommand = new ValueCommandImpl(targetItem.getControllerId(), targetItem.getThingId(), values);

        automationBus.publish(new ItemCommandEvent(targetItem.getThingId(), thingCommand));

        return true;
    }

    @Override
    public Set<String> getDependentItems(SetState input) {
        input.getItemValue().getThingId();

        Set<String> dependentItems = Sets.newHashSet(evaluatorFactory.getEvaluator(input.getResolvableValue()).getDependentItems(input.getResolvableValue()));
        dependentItems.add(input.getItemValue().getThingId());
        return dependentItems;
    }
}
