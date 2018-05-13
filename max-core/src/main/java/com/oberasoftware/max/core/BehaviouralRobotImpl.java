package com.oberasoftware.max.core;

import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.behavioural.DriveBehaviour;
import com.oberasoftware.robo.api.behavioural.GripperBehaviour;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public class BehaviouralRobotImpl implements BehaviouralRobot {

    private final List<Behaviour> behaviours;
    private final Robot robot;

    public BehaviouralRobotImpl(Robot robot, List<Behaviour> behaviours) {
        this.behaviours = behaviours;
        this.robot = robot;
    }

    public void initialize() {
        behaviours.forEach(b -> b.initialize(this, robot));
    }

    @Override
    public List<Behaviour> getBehaviours() {
        return behaviours;
    }

    @Override
    public <T extends Behaviour> T getBehaviour(Class<T> behaviourClass) {
        Optional<Behaviour> optB = behaviours.stream()
                .filter(b -> behaviourClass.isAssignableFrom(b.getClass())).findFirst();
        return optB.map(behaviourClass::cast).orElse(null);
    }

    @Override
    public <T extends GripperBehaviour> Optional<T> getGripper() {
        for (Behaviour behaviour : behaviours) {
            if(GripperBehaviour.class.isAssignableFrom(behaviour.getClass())) {
                return Optional.of((T) behaviour);
            }
        }
        return null;
    }

    @Override
    public Optional<DriveBehaviour> getWheels() {
        return Optional.of(getBehaviour(DriveBehaviour.class));
    }

    @Override
    public String getRobotId() {
        return robot.getName();
    }
}
