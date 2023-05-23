package com.oberasoftware.trainautomation;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.events.impl.ThingValueEventImpl;
import com.oberasoftware.trainautomation.api.LocEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocEventListener implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LocEventListener.class);

    @Autowired
    private LocThingRepository locThingRepository;

    @Autowired
    private LocalEventBus eventBus;

    @Autowired
    private AgentControllerInformation agentControllerInformation;

    @EventSubscribe
    public void receive(LocEvent locEvent) {
        LOG.info("Received loc event: {}", locEvent);

        int locAddress = locEvent.getLocAddress();
        String controllerId = agentControllerInformation.getControllerId();

        var thingList = locThingRepository.getLocomotiveForLocAddress(controllerId, locAddress);

        thingList.forEach(t -> {
            var thingEvent = new ThingValueEventImpl(controllerId, t.getThingId(), locEvent.getValue(), locEvent.getAttribute());
            LOG.debug("Publishing thing event: {}", thingEvent);
            eventBus.publish(thingEvent);
        });
    }
}
