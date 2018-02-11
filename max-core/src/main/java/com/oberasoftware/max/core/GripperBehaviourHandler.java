package com.oberasoftware.max.core;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.api.commands.BasicCommand;
import com.oberasoftware.home.api.model.impl.BasicCommandImpl;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.max.core.behaviours.gripper.GripperBehaviour;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

import static com.oberasoftware.home.util.ConverterHelper.mapFromJson;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class GripperBehaviourHandler implements EventHandler {
    private static final Logger LOG = getLogger(GripperBehaviourHandler.class);

    private final BehaviouralRobotRegistry robotRegistry;

    @Autowired
    public GripperBehaviourHandler(BehaviouralRobotRegistry robotRegistry) {
        this.robotRegistry = robotRegistry;
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "gripper", label = "open")
    public void openGripper(MQTTMessage mqttMessage) {
        LOG.info("Opening gripper");
        doAction(mqttMessage, GripperBehaviour::open);
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "gripper", label = "close")
    public void closeGripper(MQTTMessage mqttMessage) {
        LOG.info("Closing gripper");
        doAction(mqttMessage, GripperBehaviour::close);
    }

    private void doAction(MQTTMessage mqttMessage, Consumer<GripperBehaviour> b) {
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        Optional<BehaviouralRobot> robot = robotRegistry.getRobot(basicCommand.getControllerId());
        Optional<GripperBehaviour> obehaviour = robot.flatMap(BehaviouralRobot::getGripper);

        obehaviour.ifPresent(b);
    }
}