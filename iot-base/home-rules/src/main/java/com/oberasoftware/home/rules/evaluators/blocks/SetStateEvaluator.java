package com.oberasoftware.home.rules.evaluators.blocks;

import com.google.common.collect.Sets;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.rules.api.general.SetState;
import com.oberasoftware.home.rules.api.values.ItemValue;
import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.evaluators.EvaluatorFactory;
import com.oberasoftware.home.rules.evaluators.values.ValueEvaluator;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import com.oberasoftware.iot.core.legacymodel.Value;
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
        ItemValue targetItem = input.getItemValue();

        ValueEvaluator<ResolvableValue> valueEvaluator = evaluatorFactory.getEvaluator(input.getResolvableValue());
        Map<String, Value> values = new HashMap<>();
        values.put(targetItem.getLabel(), valueEvaluator.eval(input.getResolvableValue()));

        ItemCommand itemCommand = new ValueCommandImpl(targetItem.getControllerId(), targetItem.getItemId(), values);

        automationBus.publish(new ItemCommandEvent(targetItem.getItemId(), itemCommand));

        return true;
    }

    @Override
    public Set<String> getDependentItems(SetState input) {
        input.getItemValue().getItemId();

        Set<String> dependentItems = Sets.newHashSet(evaluatorFactory.getEvaluator(input.getResolvableValue()).getDependentItems(input.getResolvableValue()));
        dependentItems.add(input.getItemValue().getItemId());
        return dependentItems;
    }
}
