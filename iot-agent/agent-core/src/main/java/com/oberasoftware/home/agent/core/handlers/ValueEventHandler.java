package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import com.oberasoftware.iot.core.events.ThingMultiValueEvent;
import com.oberasoftware.iot.core.events.ThingValueEvent;
import com.oberasoftware.iot.core.events.ItemValueEvent;
import com.oberasoftware.iot.core.model.states.Value;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ValueEventHandler implements EventHandler {
    private static final Logger LOG = getLogger(ValueEventHandler.class);

    @Autowired
    private MQTTTopicEventBus topicEventBus;

    @EventSubscribe
    public void receive(ThingValueEvent event) {
        LOG.debug("Received a Thing value event: {}, forwarding to state topic", event);

        topicEventBus.publish(event);
    }

    @EventSubscribe
    public void receive(ThingMultiValueEvent event) {
        LOG.debug("Received a Thing Multi Value event: {}, forwarding to state topic", event);

        topicEventBus.publish(event);
    }

    @EventSubscribe
    public void receive(ItemValueEvent event) {
        LOG.debug("Received an item value event: {}", event);
        String label = event.getAttribute();
        Value value = event.getValue();

        LOG.debug("Updating state of group: {}", event.getItemId());
//        stateManager.updateItemState(event.getItemId(), label, value);
    }
}
