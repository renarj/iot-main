package com.oberasoftware.home.core.state;

import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.events.StateUpdateEvent;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.managers.StateStore;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class StateManagerImpl implements StateManager {
    private static final Logger LOG = getLogger(StateManagerImpl.class);
    private static final String LAST_SEEN = "lastSeen";

    private ConcurrentMap<String, StateImpl> itemStates = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private List<StateStore> stateStores;
    @Autowired
    private LocalEventBus automationBus;

    @Override
    public State updateItemState(String controllerId, String thingId, String attribute, Value value) {
        LOG.debug("Update state for controller: {} and thing: {} attribute: {} and value: {}", controllerId, thingId, attribute, value);

        boolean updated = updateState(controllerId, thingId, attribute, value);

        State state = getState(controllerId, thingId).orElseThrow(() -> new RuntimeIOTException("Unable to find state"));
        if(updated) {
            automationBus.publish(new StateUpdateEvent(state, attribute));

            updateStateStores(controllerId, thingId, attribute, value);
        }

        return state;
    }

    @Override
    public State updateItemState(IotThing item, String attribute, Value value) {
        return this.updateItemState(item.getControllerId(), item.getThingId(), attribute, value);
    }

    @Override
    public Map<String, State> getStates(String controllerId) {
        return itemStates.entrySet().stream()
                .filter(e -> e.getKey().startsWith(controllerId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Optional<State> getState(String controllerId, String thingId) {
        LOG.info("Retrieving item state for controller: {} and thing: {}", controllerId, thingId);
        return Optional.ofNullable(itemStates.get(key(controllerId, thingId)));
    }

    private boolean updateState(String controllerId, String thingId, String attribute, Value value) {
        LOG.debug("Updating state of item: {} with attribute: {} to value: {}", thingId, attribute, value);
        itemStates.putIfAbsent(key(controllerId, thingId), new StateImpl(controllerId, thingId));
        StateImpl state = itemStates.get(key(controllerId, thingId));
        state.updateIfChanged(LAST_SEEN, new StateItemImpl(LAST_SEEN, new ValueImpl(VALUE_TYPE.NUMBER, System.currentTimeMillis())));

        return state.updateIfChanged(attribute, new StateItemImpl(attribute, value));
    }
    private String key(String controllerId, String thingId) {
        return controllerId + "-" + thingId;
    }
    private void updateStateStores(String controllerId, String thingId, String attribute, Value value) {
        if(stateStores != null) {
            stateStores.forEach(s -> s.store(controllerId, thingId, attribute, value));
        }
    }
}
