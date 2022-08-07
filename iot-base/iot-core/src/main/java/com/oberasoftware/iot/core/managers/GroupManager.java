package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GroupManager extends GenericItemManager<GroupItem> {

    List<IotThing> getThings(String groupId);

}
