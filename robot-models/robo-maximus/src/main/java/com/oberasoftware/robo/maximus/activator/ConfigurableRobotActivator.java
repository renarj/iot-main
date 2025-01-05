package com.oberasoftware.robo.maximus.activator;

import com.google.common.collect.Lists;
import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.robo.maximus.ConfigurableRobotBuilder;
import com.oberasoftware.robo.maximus.ServoRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigurableRobotActivator implements Activator {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableRobotActivator.class);

    @Autowired
    private AgentClient agentClient;

    @Autowired
    private ServoRegistry servoRegistry;

    @Override
    public String getSchemaId() {
        return "ConfigurableRobot";
    }

    @Override
    public void activate(RobotContext context, IotThing activatable) {
        LOG.info("Activating robot: {}", activatable.getThingId());

        activateJoints(context, activatable);
    }

    private void activateJoints(RobotContext context, IotThing activatable) {
        try {
            List<IotThing> joints = agentClient.getChildren(activatable.getControllerId(), activatable.getThingId(), "Joint");
            LOG.info("Discovered joints: {} for robot: {}", joints, activatable.getThingId());

            var jbs = joints.stream().map(j -> {
                String servoThingId = j.getProperty("servo");
                String servoId = servoRegistry.getThing(activatable.getControllerId(), servoThingId).getServoId();
                return ConfigurableRobotBuilder.JointBuilder.create(j.getThingId(), servoId, j.getFriendlyName());
            }).collect(Collectors.toList());
            context.getRobotBuilder().joints("default", jbs);
        } catch (IOTException e) {
            LOG.error("Could not retrieve robot joints", e);
        }
    }

    @Override
    public List<IotThing> getDependents(RobotContext context, IotThing activatable) {
        List<IotThing> thingsToActivate = new ArrayList<>();
        thingsToActivate.addAll(getServoDependencies(activatable));
        thingsToActivate.addAll(getWheels(activatable));

        return thingsToActivate;
    }

    private List<IotThing> getWheels(IotThing activatable) {
        try {
            return agentClient.getChildren(activatable.getControllerId(), activatable.getThingId(), "wheel");
        } catch (IOTException e) {
            LOG.error("Could not retrieve remote Wheel information", e);
        }
        return Lists.newArrayList();
    }

    private List<IotThing> getServoDependencies(IotThing activatable) {
        List<IotThing> thingsToActivate = new ArrayList<>();

        String servoDriver = activatable.getProperty("servoDriver");
        if (servoDriver != null && !servoDriver.isEmpty()) {
            LOG.info("Found servo driver for activation");
            try {
                var oDriver = agentClient.getThing(activatable.getControllerId(), servoDriver);
                oDriver.ifPresent(thingsToActivate::add);

                var sensors = agentClient.getChildren(activatable.getControllerId(), activatable.getThingId(), "sensor");
                thingsToActivate.addAll(sensors);
            } catch (IOTException e) {
                LOG.error("Could not retrieve remote servo driver information", e);
            }
        } else {
            LOG.warn("No servo driver found to configure");
        }
        return thingsToActivate;
    }
}


