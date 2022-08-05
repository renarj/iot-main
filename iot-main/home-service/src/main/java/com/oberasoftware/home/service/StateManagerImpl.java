package com.oberasoftware.home.service;

import com.google.common.collect.Maps;
import com.oberasoftware.iot.core.AutomationBus;
import com.oberasoftware.iot.core.managers.StateManager;
import com.oberasoftware.iot.core.managers.StateStore;
import com.oberasoftware.iot.core.legacymodel.Status;
import com.oberasoftware.iot.core.legacymodel.impl.StateImpl;
import com.oberasoftware.iot.core.legacymodel.impl.StateItemImpl;
import com.oberasoftware.iot.core.events.StateUpdateEvent;
import com.oberasoftware.iot.core.legacymodel.State;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.iot.core.model.IotThing;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class StateManagerImpl implements StateManager {
    private static final Logger LOG = getLogger(StateManagerImpl.class);

    private ConcurrentMap<String, StateImpl> itemStates = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private List<StateStore> stateStores;

    @Autowired
    private AutomationBus automationBus;

    private boolean updateState(String itemId, String label, Value value) {
        LOG.debug("Updating state of item: {} with label: {} to value: {}", itemId, label, value);
        itemStates.putIfAbsent(itemId, new StateImpl(itemId));
        StateImpl state = itemStates.get(itemId);

        return state.updateIfChanged(label, new StateItemImpl(label, value));
    }


    @Override
    public State updateItemState(String itemId, String label, Value value) {
        boolean updated = updateState(itemId, label, value);
        State state = itemStates.get(itemId);

        if(updated) {
            automationBus.publish(new StateUpdateEvent(state));
        }

        return itemStates.get(itemId);
    }

    @Override
    public State updateDeviceState(IotThing item, String label, Value value) {
        boolean updated = updateState(item.getId(), label, value);

        StateImpl state = itemStates.get(item.getId());
        if(updated) {
            updateStateStores(item, label, value);

            automationBus.publish(new StateUpdateEvent(state));
        }

        return state;
    }

    @Override
    public State updateStatus(IotThing item, Status newStatus) {
        String itemId = item.getId();

        if(itemStates.containsKey(itemId)) {
            State oldState = itemStates.get(itemId);

            StateImpl newState = new StateImpl(itemId);
            oldState.getStateItems().forEach(si -> newState.updateIfChanged(si.getLabel(), si));

            itemStates.put(itemId, newState);
        } else {
            itemStates.put(itemId, new StateImpl(itemId));
        }


        return null;
    }

    @Override
    public Map<String, State> getStates() {
        return Maps.newHashMap(itemStates);
    }

    @Override
    public State getState(String itemId) {
        return itemStates.get(itemId);
    }

    private void updateStateStores(IotThing item, String label, Value value) {
        if(stateStores != null) {
            stateStores.forEach(s -> s.store(item.getId(), item.getControllerId(), item.getPluginId(), item.getId(), label, value));
        }
    }
}
