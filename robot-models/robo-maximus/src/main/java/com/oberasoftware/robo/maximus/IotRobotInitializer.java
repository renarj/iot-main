package com.oberasoftware.robo.maximus;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.events.impl.ThingMultiValueEventImpl;
import com.oberasoftware.iot.core.events.impl.ThingValueEventImpl;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.behavioural.JointBasedRobotRegistery;
import com.oberasoftware.iot.core.robotics.commands.Scale;
import com.oberasoftware.iot.core.robotics.humanoid.JointBasedRobot;
import com.oberasoftware.iot.core.robotics.servo.ServoData;
import com.oberasoftware.iot.core.robotics.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.maximus.activator.Activator;
import com.oberasoftware.robo.maximus.activator.ActivatorFactory;
import com.oberasoftware.robo.maximus.activator.RobotContext;
import com.oberasoftware.robo.maximus.model.JointDataImpl;
import com.oberasoftware.robo.maximus.model.SensorDataImpl;
import com.oberasoftware.robo.maximus.storage.MotionStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IotRobotInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(IotRobotInitializer.class);

    private final AgentClient agentClient;

    private final ApplicationContext applicationContext;

    private final RobotRegistry robotRegistry;

    private final JointBasedRobotRegistery jointBasedRobotRegistery;

    private final LocalEventBus eventBus;

    private final ServoRegistry servoRegistry;

    private final ActivatorFactory activatorFactory;

    private final AgentControllerInformation controllerInformation;

    public IotRobotInitializer(AgentClient agentClient, ApplicationContext applicationContext, RobotRegistry robotRegistry, JointBasedRobotRegistery jointBasedRobotRegistery, AgentControllerInformation controllerInformation, LocalEventBus eventBus, ServoRegistry servoRegistry, ActivatorFactory activatorFactory) {
        this.agentClient = agentClient;
        this.applicationContext = applicationContext;
        this.robotRegistry = robotRegistry;
        this.jointBasedRobotRegistery = jointBasedRobotRegistery;
        this.controllerInformation = controllerInformation;
        this.servoRegistry = servoRegistry;
        this.eventBus = eventBus;
        this.activatorFactory = activatorFactory;
    }

    public void initialize() {
        try {
            LOG.info("Retrieving all configured robots for controller: {}", controllerInformation.getControllerId());
            var robots = agentClient.getThings(controllerInformation.getControllerId(), RobotExtension.ROBOT_EXTENSION, "robot");
            LOG.info("Robots found: {}", robots);

            robots.forEach(r -> {
                LOG.info("Configuring robot: {} on controller: {}", r.getThingId(), r.getControllerId());
                HardwareRobotBuilder hardwareRobotBuilder = new HardwareRobotBuilder(r.getThingId(), applicationContext);
                hardwareRobotBuilder.capability(MotionStorage.class);
                JointBasedRobotBuilder robotBuilder = new JointBasedRobotBuilder(r.getControllerId(), r.getThingId());
                var context = new RobotContext(hardwareRobotBuilder, robotBuilder);
                activatorFactory.getActivator(r).ifPresent(a -> initRobot(a, context, r));

                var hardwareRobot = hardwareRobotBuilder.build();
                LOG.info("All dependencies for robot: {} are configured, building robot construct", r.getThingId());
                JointBasedRobot robot = robotBuilder.build(hardwareRobot);
                jointBasedRobotRegistery.register(robot);

                hardwareRobot.listen(new RobotEventListener());
                robotRegistry.register(hardwareRobot);

                LOG.info("Robot: {} configured", r.getThingId());
            });
        } catch(Exception e) {
            LOG.error("", e);
        }
    }

    private void initRobot(Activator rootActivator, RobotContext context, IotThing activatable) {
        List<IotThing> dependents = rootActivator.getDependents(context, activatable);
        dependents.forEach(d -> activatorFactory.getActivator(d)
                .ifPresent(a -> a.activate(context, d)));
        rootActivator.activate(context, activatable);
    }

    public class RobotEventListener implements EventHandler {
        @EventSubscribe
        public void receiveStateUpdate(ServoUpdateEvent stateUpdateEvent) {
            LOG.debug("Received servo update event: {}", stateUpdateEvent);
            ServoData data = stateUpdateEvent.getServoData();
            var servoId = data.getServoId();

            servoRegistry.getThings(servoId).forEach(t -> {
                Map<String, Value> valueMap = new HashMap<>();
                stateUpdateEvent.getServoData().getValues().entrySet().stream()
                        .filter(e -> !(e.getValue() instanceof Scale))
                        .forEach(e ->
                            valueMap.put(e.getKey().name().toLowerCase(), new ValueImpl(VALUE_TYPE.NUMBER, e.getValue())));

                var v = new ThingMultiValueEventImpl(t.getControllerId(), t.getThingId(), valueMap);
                LOG.debug("Sending servo value: {}", v);
                eventBus.publish(v);
            });
        }

        @EventSubscribe
        public void receiveJointUpdate(JointDataImpl jointData) {
            var v = jointData.getValues();
            var event = new ThingMultiValueEventImpl(jointData.getControllerId(), jointData.getId(), v);
            LOG.debug("Publishing joint event: {}", event);
            eventBus.publish(event);
        }

        @EventSubscribe
        public void receiveSensorData(SensorDataImpl sensorData) {
            var event = new ThingValueEventImpl(sensorData.getControllerId(), sensorData.getThingId(), sensorData.getValue(), sensorData.getAttribute());
            LOG.info("Publishing sensor data: {}", event);
            eventBus.publish(event);
        }
    }

}
