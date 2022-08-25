package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MQTTCommandListener implements EventHandler {
    private static final Logger LOG = getLogger( MQTTCommandListener.class );

    @Autowired
    private LocalEventBus eventBus;

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS)
    public void receive(MQTTMessage message) {
        LOG.info("Received a MQTT message: {}", message);

        var content = message.getMessage();
        var basicCommand = mapFromJson(content, BasicCommandImpl.class);
        LOG.info("Basic command received: {}", basicCommand);
        eventBus.publish(basicCommand);
    }
}
