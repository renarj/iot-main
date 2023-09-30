package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.managers.GroupManager;
import com.oberasoftware.iot.core.managers.ThingManager;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.GroupItem;
import com.oberasoftware.iot.core.model.storage.impl.GroupItemImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@RestController
@RequestMapping("/api")
public class GroupRestSvc {
    private static final Logger LOG = getLogger(GroupRestSvc.class);

    @Autowired
    private GroupManager groupManager;

    @Autowired
    private ThingManager thingManager;

    @RequestMapping(value = "/groups")
    public List<? extends GroupItem> findAllGroups() {
        return groupManager.getItems();
    }

    @RequestMapping(value = "/groups({groupId})")
    public GroupItem findGroup(@PathVariable String groupId) {
        return groupManager.getItem(groupId);
    }


    @RequestMapping(value = "/groups/controller({controllerId})")
    public List<? extends GroupItem> findGroupsByController(@PathVariable String controllerId) {
        return groupManager.getItems(controllerId);
    }

    @RequestMapping(value = "/groups({groupId})/things")
    public List<? extends IotThing> findDevices(@PathVariable String groupId) {
        GroupItem groupItem = groupManager.getItem(groupId);

        return groupItem.getDeviceIds().stream()
                .map(d -> thingManager.findThing(groupItem.getControllerId(), d).get()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/groups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GroupItem createGroup(@RequestBody GroupItemImpl item) {
        return groupManager.store(item);
    }

    @RequestMapping(value = "/groups({groupId})", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroup(@PathVariable String groupId) {
        LOG.debug("Deleting group: {}", groupId);
        groupManager.delete(groupId);
        LOG.debug("Deleted group");
    }


}
