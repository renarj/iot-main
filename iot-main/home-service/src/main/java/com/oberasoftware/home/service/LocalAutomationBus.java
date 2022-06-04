package com.oberasoftware.home.service;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.api.AutomationBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author renarj
 */
@Component
public class LocalAutomationBus implements AutomationBus {
    @Autowired
    private LocalEventBus eventBus;



    @Override
    public void publish(Event event) {
        eventBus.publish(event);
    }

    @Override
    public void registerHandler(EventHandler handler) {
        eventBus.registerHandler(handler);
    }
}
