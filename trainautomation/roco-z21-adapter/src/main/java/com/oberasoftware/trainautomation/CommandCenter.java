package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.model.IotThing;

public interface CommandCenter {
    String getId();

    void connect(IotThing commandCenter);

    void handleCommand(ItemValueCommand command);
}
