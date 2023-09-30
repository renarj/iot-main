package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.managers.GroupManager;
import com.oberasoftware.iot.core.managers.ThingManager;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Renze de Vries
 */
@Component
public class GroupManagerImpl implements GroupManager {

    @Autowired
    private ThingManager thingManager;

    @Override
    public List<? extends GroupItem> getItems() {
        return null;
    }

    @Override
    public List<? extends GroupItem> getItems(String controllerId) {
        return null;
    }

    @Override
    public GroupItem getItem(String itemId) {
        return null;
    }

    @Override
    public GroupItem store(GroupItem item) {
        return null;
    }

    @Override
    public void delete(String itemId) {

    }

    @Override
    public List<IotThing> getThings(String groupId) {
        List<String> devicesIds = getItem(groupId).getDeviceIds();
        return devicesIds.stream().map(di -> {
            var split = di.split("-");
            var controller = split[0];
            var itemId = split[1];
            return thingManager.findThing(controller, itemId).get();
        }).collect(Collectors.toList());
    }
}
