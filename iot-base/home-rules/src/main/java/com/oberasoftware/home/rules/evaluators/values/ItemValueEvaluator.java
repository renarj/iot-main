package com.oberasoftware.home.rules.evaluators.values;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.home.rules.api.values.ItemValue;
import com.oberasoftware.home.rules.evaluators.EvalException;
import com.oberasoftware.iot.core.model.states.State;
import com.oberasoftware.iot.core.model.states.StateItem;
import com.oberasoftware.iot.core.model.states.Value;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@Component
public class ItemValueEvaluator implements ValueEvaluator<ItemValue> {
    private static final Logger LOG = getLogger(ItemValueEvaluator.class);

    @Autowired
    private StateManager stateManager;

    @Override
    public Value eval(ItemValue input) {
        String itemId = input.getItemId();
        String label = input.getLabel();

        LOG.debug("Retrieving item: {} state value for label: {}", itemId, label);

        State state = stateManager.getState(input.getControllerId(), itemId);
        if(state != null) {
            StateItem stateItem = state.getStateItem(label);

            if(stateItem != null) {
                return stateItem.getValue();
            }
        }

        throw new EvalException("Could not evaluate item: " + itemId + " label: " + label + " no values present");
    }

    @Override
    public Set<String> getDependentItems(ItemValue input) {
        return Sets.newHashSet(input.getItemId());
    }
}
