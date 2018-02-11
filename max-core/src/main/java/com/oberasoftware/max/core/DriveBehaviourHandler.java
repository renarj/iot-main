package com.oberasoftware.max.core;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.api.commands.BasicCommand;
import com.oberasoftware.home.api.model.impl.BasicCommandImpl;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.home.util.IntUtils;
import com.oberasoftware.max.core.behaviours.wheels.DriveBehaviour;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiConsumer;

import static com.oberasoftware.home.util.ConverterHelper.mapFromJson;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class DriveBehaviourHandler implements EventHandler {
    private static final Logger LOG = getLogger(DriveBehaviourHandler.class);

    private final BehaviouralRobotRegistry robotRegistry;

    @Autowired
    public DriveBehaviourHandler(BehaviouralRobotRegistry robotRegistry) {
        this.robotRegistry = robotRegistry;
    }

    //    @EventSubscribe
//    @MQTTPath(group = MessageGroup.COMMANDS, device = "drive", label = "forward")
//    public void forward(MQTTMessage mqttMessage) {
//        doAction(mqttMessage, DriveBehaviour::forward);
//    }
//
//    @EventSubscribe
//    @MQTTPath(group = MessageGroup.COMMANDS, device = "drive", label = "backward")
//    public void back(MQTTMessage mqttMessage) {
//        doAction(mqttMessage, DriveBehaviour::backward);
//    }
//
//    @EventSubscribe
//    @MQTTPath(group = MessageGroup.COMMANDS, device = "drive", label = "left")
//    public void left(MQTTMessage mqttMessage) {
//        doAction(mqttMessage, DriveBehaviour::left);
//    }
//
    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "drive", label = "direction")
    public void right(MQTTMessage message) {
        BasicCommand basicCommand = toCommand(message);
        DriveBehaviour.DIRECTION direction = DriveBehaviour.DIRECTION.fromString(basicCommand.getProperty("direction"));

        doAction(basicCommand, (d, s) -> d.drive(s, direction));
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "drive", label = "stop")
    public void stop(MQTTMessage mqttMessage) {
        doAction(toCommand(mqttMessage), (d, s) -> d.stop());
    }

    private BasicCommand toCommand(MQTTMessage message) {
        return mapFromJson(message.getMessage(), BasicCommandImpl.class);
    }

    private void doAction(BasicCommand basicCommand, BiConsumer<DriveBehaviour, Integer> b) {
        int speed = IntUtils.toInt(basicCommand.getProperty("speed"), 512);

        Optional<BehaviouralRobot> robot = robotRegistry.getRobot(basicCommand.getControllerId());
        Optional<DriveBehaviour> w = robot.flatMap(BehaviouralRobot::getWheels);

        LOG.info("Doing action: {} to robot: {}", b, robot);
        w.ifPresent(ow -> b.accept(ow, speed));
    }
}
