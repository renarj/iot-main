package com.oberasoftware.iot.core.commands.impl;

import com.oberasoftware.iot.core.commands.GroupCommand;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.iot.core.model.storage.GroupItem;

/**
 * @author Renze de Vries
 */
public class GroupCommandImpl implements GroupCommand {

    private final ItemCommand command;
    private final GroupItem groupItem;

    public GroupCommandImpl(ItemCommand command, GroupItem groupItem) {
        this.command = command;
        this.groupItem = groupItem;
    }

    @Override
    public ItemCommand getCommand() {
        return command;
    }

    @Override
    public GroupItem getGroup() {
        return groupItem;
    }
}
