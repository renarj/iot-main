package com.oberasoftware.robo.maximus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.AgentControllerInformation;
import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.events.impl.ThingMultiValueEventImpl;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.servo.ServoData;
import com.oberasoftware.iot.core.robotics.servo.events.ServoUpdateEvent;
import com.oberasoftware.robo.core.HardwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.ServoSensorDriver;
import com.oberasoftware.robo.dynamixel.DynamixelServoDriver;
import com.oberasoftware.robo.dynamixel.DynamixelTorgueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RobotInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(RobotInitializer.class);

    private final ThingClient thingClient;

    private final ApplicationContext applicationContext;

    private final RobotRegistry robotRegistry;

    private final LocalEventBus eventBus;

    private final ServoRegistry servoRegistry = new ServoRegistry();

    private final ActivatorFactory activatorFactory;

    private final AgentControllerInformation controllerInformation;

    public RobotInitializer(ThingClient thingClient, ApplicationContext applicationContext, RobotRegistry robotRegistry, AgentControllerInformation controllerInformation, LocalEventBus eventBus) {
        this.thingClient = thingClient;
        this.applicationContext = applicationContext;
        this.robotRegistry = robotRegistry;
        this.controllerInformation = controllerInformation;
        this.activatorFactory = new ActivatorFactory(Lists.newArrayList(new RobotArmActivator(), new DynamixelActivator()));
        this.eventBus = eventBus;
    }


    public void initialize() {
        try {
            LOG.info("Retrieving all configured robots for controller: {}", controllerInformation.getControllerId());
            var robots = thingClient.getThings(controllerInformation.getControllerId(), RobotExtension.ROBOT_EXTENSION, "robot");
            LOG.info("Robots found: {}", robots);

            robots.forEach(r -> {
                LOG.info("Configuring robot: {} on controller: {}", r.getThingId(), r.getControllerId());
                HardwareRobotBuilder hardwareRobotBuilder = new HardwareRobotBuilder(applicationContext);
                JointBasedRobotBuilder robotBuilder = new JointBasedRobotBuilder();
                var context = new RobotContext(hardwareRobotBuilder, robotBuilder);
                activatorFactory.getActivator(r).ifPresent(a -> a.initRobot(context, r));

                var hardwareRobot = hardwareRobotBuilder.build();
                LOG.info("All dependencies for robot: {} are configured, building robot construct", r.getThingId());
//                robotBuilder.build(hardwareRobot);


                hardwareRobot.listen(new ServoUpdateListener());

                LOG.info("Robot: {} configured", r.getThingId());
            });
        } catch (IOTException e) {
            LOG.error("", e);
        } catch(Exception e) {
            LOG.error("", e);
        }
    }

    private class ThingKey {
        private final String controllerId;
        private final String thingId;

        public ThingKey(String controllerId, String thingId) {
            this.controllerId = controllerId;
            this.thingId = thingId;
        }
    }

    private class ServoRegistry {
        private Multimap<String, ThingKey> servoRegister = ArrayListMultimap.create();

        public void addServo(String controllerId, String thingId, String servoId) {
            servoRegister.put(servoId, getKey(controllerId, thingId));
        }

        public List<ThingKey> getThings(String servoId) {
            return Lists.newArrayList(servoRegister.get(servoId));
        }

        private ThingKey getKey(String controllerId, String thingId) {
            return new ThingKey(controllerId, thingId);
        }
    }

    public class ServoUpdateListener implements EventHandler {
        @EventSubscribe
        public void receiveStateUpdate(ServoUpdateEvent stateUpdateEvent) {
            LOG.info("Received servo update event: {}", stateUpdateEvent);
            ServoData data = stateUpdateEvent.getServoData();
            var servoId = data.getServoId();

            servoRegistry.getThings(servoId).forEach(t -> {
                Map<String, Value> valueMap = new HashMap<>();
                stateUpdateEvent.getServoData().getValues().forEach((k, v) ->
                        valueMap.put(k.name(), new ValueImpl(VALUE_TYPE.NUMBER, v)));

                var v = new ThingMultiValueEventImpl(t.controllerId, t.thingId, valueMap);
                LOG.info("Sending servo value: {}", v);
                eventBus.publish(v);
            });
        }
    }

    public class RobotContext {
        private final HardwareRobotBuilder hardwareBuilder;
        private final RobotBuilder robotBuilder;

        public RobotContext(HardwareRobotBuilder hardwareBuilder, RobotBuilder robotBuilder) {
            this.hardwareBuilder = hardwareBuilder;
            this.robotBuilder = robotBuilder;
        }

        public HardwareRobotBuilder getHardwareBuilder() {
            return hardwareBuilder;
        }

        public RobotBuilder getRobotBuilder() {
            return robotBuilder;
        }
    }

    private class ActivatorFactory {

        private final List<Activator> activators;

        public ActivatorFactory(List<Activator> activators) {
            this.activators = activators;
        }

        public Optional<Activator> getActivator(IotThing thing) {
            return activators.stream()
                    .filter(a -> a.getSchemaId().equalsIgnoreCase(thing.getSchemaId()))
                    .findFirst();
        }
    }

    abstract class Activator {
        abstract String getSchemaId();

        abstract List<IotThing> getDependents(RobotContext context, IotThing activatable);

        abstract void activate(RobotContext context, IotThing activatable);

        void initRobot(RobotContext context, IotThing activatable) {
            List<IotThing> dependents = getDependents(context, activatable);
            dependents.forEach(d -> activatorFactory.getActivator(d)
                    .ifPresent(a -> a.activate(context, d)));
            activate(context, activatable);
        }
    }

    private class DynamixelActivator extends Activator {
        @Override
        public String getSchemaId() {
            return "DynamixelServoDriver";
        }

        @Override
        List<IotThing> getDependents(RobotContext context, IotThing activatable) {
            return new ArrayList<>();
        }

        @Override
        void activate(RobotContext context, IotThing driver) {
            LOG.info("Configuring dynamixel servo driver: {}", driver.getThingId());
            try {
                String port = driver.getProperty("DXL_PORT");
                List<IotThing> servos = thingClient.getChildren(driver.getControllerId(), driver.getThingId(), "servo");
                servos.forEach(s -> servoRegistry.addServo(s.getControllerId(), s.getThingId(), s.getProperty("servo_id")));
                String motorString = servos.stream().map(s -> s.getProperty("servo_id")).collect(Collectors.joining(","));
                LOG.info("Registering Dynamixel servo driver for servos: {} on port: {}", motorString, port);

                context.getHardwareBuilder()
                        .servoDriver(DynamixelServoDriver.class,
                                ImmutableMap.<String, String>builder()
                                        .put(DynamixelServoDriver.PORT, port)
                                        .put("motors", motorString)
                                        .build())
                        .capability(ServoSensorDriver.class)
                        .capability(DynamixelTorgueManager.class);
            } catch (IOTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class RobotArmActivator extends Activator {
        @Override
        public String getSchemaId() {
            return "RobotArm";
        }

        @Override
        void activate(RobotContext context, IotThing activatable) {
            LOG.info("Activating robot: {}", activatable.getThingId());
            context.getHardwareBuilder().name(activatable.getThingId());
        }

        @Override
        List<IotThing> getDependents(RobotContext context, IotThing activatable) {
            List<IotThing> thingsToActivate = new ArrayList<>();
            String servoDriver = activatable.getProperty("servoDriver");
            if(servoDriver != null && !servoDriver.isEmpty()) {
                LOG.info("Found servo driver for activation");
                try {
                    var oDriver = thingClient.getThing(activatable.getControllerId(), servoDriver);
                    oDriver.ifPresent(thingsToActivate::add);
                } catch (IOTException e) {
                    LOG.error("Could not retrieve remote servo driver information", e);
                }
            } else {
                LOG.warn("No servo driver found to configure");
            }
            return thingsToActivate;
        }

    }
}
