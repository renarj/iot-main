package com.oberasoftware.iot.core.model.states;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author renarj
 */
public class StateImpl implements State {

    private String itemId;
    private String controllerId;
    private Map<String, StateItemImpl> stateItems = new HashMap<>();

    public StateImpl(String controllerId, String itemId) {
        this.controllerId = controllerId;
        this.itemId = itemId;
    }

    public StateImpl() {

    }

    @Override
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getControllerId() {
        return this.controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public List<StateItem> getStateItems() {
        return Lists.newArrayList(stateItems.values());
    }

    public void setStateItems(List<StateItemImpl> stateItems) {
        stateItems.forEach(si -> {
            this.stateItems.put(si.getAttribute().toLowerCase(), si);
        });
    }

    @Override
    public StateItem getStateItem(String attribute) {
        return stateItems.get(attribute.toLowerCase());
    }

    public boolean updateIfChanged(String attribute, StateItemImpl stateItem) {
        String normalisedAttribute = attribute.toLowerCase();

        boolean updated = true;
        if(stateItems.containsKey(normalisedAttribute)) {
            //if the existing item is different than new item it is updated
            updated = !stateItems.get(normalisedAttribute).equals(stateItem);
        }
        this.stateItems.put(normalisedAttribute, stateItem);
        return updated;
    }

    @Override
    public String toString() {
        return "StateImpl{" +
                "itemId='" + itemId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", stateItems=" + stateItems +
                '}';
    }
}
