package com.oberasoftware.trainautomation;

import com.oberasoftware.iot.core.model.IotThing;

public interface CommandCenter {
    String getId();

    void connect(IotThing commandCenter);

    void handleCommand(TrainCommand command);
}
