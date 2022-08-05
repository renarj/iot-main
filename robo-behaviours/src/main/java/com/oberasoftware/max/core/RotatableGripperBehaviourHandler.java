package com.oberasoftware.max.core;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTPath;
import com.oberasoftware.home.core.mqtt.MessageGroup;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.GripperBehaviour;
import com.oberasoftware.iot.core.robotics.behavioural.gripper.RotatableGripper;
import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.robo.core.model.BasicCommandImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapFromJson;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class RotatableGripperBehaviourHandler implements EventHandler {
    private static final Logger LOG = getLogger(RotatableGripperBehaviourHandler.class);

    private final BehaviouralRobotRegistry robotRegistry;

    @Autowired
    public RotatableGripperBehaviourHandler(BehaviouralRobotRegistry robotRegistry) {
        this.robotRegistry = robotRegistry;
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "gripper", label = "rotate")
    public void rotate(MQTTMessage mqttMessage) {
        LOG.info("Opening gripper");
        doAction(mqttMessage, GripperBehaviour::open);
    }

    @EventSubscribe
    @MQTTPath(group = MessageGroup.COMMANDS, device = "gripper", label = "center")
    public void center(MQTTMessage mqttMessage) {
        LOG.info("Closing gripper");
        doAction(mqttMessage, RotatableGripper::rotateLevel);
    }

    private void doAction(MQTTMessage mqttMessage, Consumer<RotatableGripper> b) {
        BasicCommand basicCommand = mapFromJson(mqttMessage.getMessage(), BasicCommandImpl.class);

        Optional<BehaviouralRobot> robot = robotRegistry.getRobot(basicCommand.getControllerId());
        Optional<RotatableGripper> obehaviour = Optional.empty(); //robot.flatMap(BehaviouralRobot::getGripper);

        obehaviour.ifPresent(b);
    }

}
