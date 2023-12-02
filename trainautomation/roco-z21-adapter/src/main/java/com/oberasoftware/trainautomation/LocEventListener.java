package com.oberasoftware.trainautomation;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.events.impl.ThingValueEventImpl;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.util.IntUtils;
import com.oberasoftware.trainautomation.api.LocEvent;
import com.oberasoftware.trainautomation.api.SensorEvent;
import com.oberasoftware.trainautomation.api.TrainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public void receive(LocEvent event) {
        LOG.debug("Received loc event: {}", event);

        translateEvents(agentControllerInformation.getControllerId(), event,
                (t) -> event.getAttribute(),
            () -> locThingRepository.getLocomotiveForLocAddress(agentControllerInformation.getControllerId(), event.getEventAddress()));
    }

    @EventSubscribe
    public void receive(SensorEvent event) {
        LOG.debug("Received sensor event: {}", event);
        translateEvents(agentControllerInformation.getControllerId(), event,
                iotThing -> getPortAttribute(iotThing, event.getEventAddress(), event.getAttribute()),
                () -> locThingRepository.getSensors(agentControllerInformation.getControllerId(), event.getEventAddress()));
    }

    private String getPortAttribute(IotThing thing, int address, String attr) {
        int nrPorts = IntUtils.toInt(thing.getProperty("ports")).orElse(1);
        var properties = thing.getProperties();
        for(int i=0; i<nrPorts; i++) {
            var key = "port" + (i + 1);
            if(properties.containsKey(key)) {
                int portAddress = IntUtils.toInt(properties.get(key)).orElse(-1);
                if(portAddress == address) {
                    return key + "-" + attr;
                }
            }

        }
        return attr;
    }

    private void translateEvents(String controllerId, TrainEvent event, Function<IotThing, String> attributeMapper, Supplier<List<IotThing>> supplier) {
        if(!supplier.get().isEmpty()) {
            supplier.get().forEach(t -> {
                String attribute = attributeMapper.apply(t);
                var thingEvent = new ThingValueEventImpl(controllerId, t.getThingId(), event.getValue(), attribute);
                LOG.debug("Publishing thing event: {}", thingEvent);
                eventBus.publish(thingEvent);
            });
        } else {
            LOG.debug("Cannot map event: {} to a known thing", event);
        }

    }
}
