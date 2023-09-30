package com.oberasoftware.home.rules.test;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.base.event.EventFilter;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class MockAutomationBus extends LocalEventBus {
    private CopyOnWriteArrayList<Event> events = new CopyOnWriteArrayList<>();
    @Override
    public void publish(Event event, Object... arguments) {
        events.add(event);
    }

    @Override
    public boolean publishSync(Event event, TimeUnit unit, long time, Object... arguments) {
        events.add(event);
        return true;
    }

    @Override
    public void registerHandler(EventHandler handler) {

    }

    @Override
    public void registerFilter(EventFilter filter) {

    }

    public List<Event> getPublishedEvents() {
        return events;
    }
}
