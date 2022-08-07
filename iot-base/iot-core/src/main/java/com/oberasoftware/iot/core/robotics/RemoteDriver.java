package com.oberasoftware.iot.core.robotics;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.iot.core.robotics.commands.CommandListener;

/**
 * @author Renze de Vries
 */
public interface RemoteDriver extends ActivatableCapability {
    void publish(Event robotEvent);

    void register(CommandListener<?> commandListener);
}
