package com.oberasoftware.home.data;

import com.oberasoftware.home.service.GenericItemManagerImpl;
import com.oberasoftware.iot.core.managers.GroupManager;
import com.oberasoftware.iot.core.managers.ItemManager;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import com.oberasoftware.iot.core.model.storage.impl.GroupItemImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Renze de Vries
 */
@Component
public class GroupManagerImpl extends GenericItemManagerImpl<GroupItem> implements GroupManager {

    @Autowired
    private ItemManager itemManager;

    @Override
    public List<IotThing> getThings(String groupId) {
        List<String> devicesIds = getItem(groupId).getDeviceIds();
        return devicesIds.stream().map(di -> {
            var split = di.split("-");
            var controller = split[0];
            var itemId = split[1];
            return itemManager.findThing(controller, itemId).get();
        }).collect(Collectors.toList());
    }

    @Override
    protected Class<? extends GroupItem> getType() {
        return GroupItemImpl.class;
    }
}
