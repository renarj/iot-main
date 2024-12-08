package com.oberasoftware.home.rules.evaluators.values;

import com.google.common.collect.Sets;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.evaluators.EvalException;
import com.oberasoftware.iot.core.client.StateClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.StateItem;
import com.oberasoftware.iot.core.model.states.Value;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class ItemValueEvaluator implements ValueEvaluator<ThingAttributeValue> {
    private static final Logger LOG = getLogger(ItemValueEvaluator.class);

    @Autowired
    private StateClient stateClient;

    @Override
    public Value eval(ThingAttributeValue input) {
        String thingId = input.getThingId();
        String label = input.getAttribute();

        LOG.info("Retrieving item: {} state value for label: {}", thingId, label);

        try {
            Optional<State> state = stateClient.getState(input.getControllerId(), thingId);
            if (state.isPresent()) {
                StateItem stateItem = state.get().getStateItem(label);

                if (stateItem != null) {
                    LOG.info("State value: {} for thing: {}", stateItem, thingId);
                    return stateItem.getValue();
                }
            }

            throw new EvalException("Could not evaluate item: " + thingId + " label: " + label + " no values present");
        } catch(IOTException e) {
            throw new EvalException("Could not evaluate item: " + thingId + " label: " + label + " could not get State", e);
        }
    }

    @Override
    public Set<String> getDependentItems(ThingAttributeValue input) {
        return Sets.newHashSet(input.getThingId());
    }
}
