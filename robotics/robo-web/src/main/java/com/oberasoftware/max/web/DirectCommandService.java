package com.oberasoftware.max.web;

import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageImpl;
import com.oberasoftware.iot.core.commands.impl.BasicCommandImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapToJson;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/command")
public class DirectCommandService {
    private static final Logger LOG = getLogger(DirectCommandService.class);

    private static final String TOPIC_PATH = "/commands/%s/%s/%s";

    @Autowired
    private LocalEventBus eventBus;

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody
    BasicCommandImpl sendCommand(@RequestBody BasicCommandImpl command) {
        LOG.info("Received a command: {} dispatching to eventbus", command);

        String topic = String.format(TOPIC_PATH, command.getControllerId(), command.getThingId(), command.getCommandType());
        LOG.info("Received a direct basic command: {} to topic: {}", command, topic);
        MQTTMessage message = new MQTTMessageImpl(topic, mapToJson(command));

        eventBus.publish(message);

        return command;
    }

}
