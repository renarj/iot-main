package com.oberasoftware.iot.core.events.impl;

import com.oberasoftware.iot.core.commands.ThingCommand;
import com.oberasoftware.iot.core.events.ItemEvent;

/**
 * @author renarj
 */
public class ItemCommandEvent implements ItemEvent {

    private final String itemId;
    private final ThingCommand command;

    public ItemCommandEvent(String itemId, ThingCommand command) {
        this.itemId = itemId;
        this.command = command;
    }

    public String getItemId() {
        return itemId;
    }

    public ThingCommand getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemCommandEvent that = (ItemCommandEvent) o;

        if (!itemId.equals(that.itemId)) return false;
        return command.equals(that.command);

    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + command.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ItemCommandEvent{" +
                "itemId='" + itemId + '\'' +
                ", command=" + command +
                '}';
    }
}
