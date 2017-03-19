package com.oberasoftware.max.core.behaviours.servos.impl;

import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.max.core.behaviours.servos.ServoBehaviour;

import java.util.List;

/**
 * @author renarj
 */
public class CompositeServoBehaviour implements ServoBehaviour {
    public List<ServoBehaviour> servoBehaviours;

    public CompositeServoBehaviour(ServoBehaviour... behaviours) {
        servoBehaviours = Lists.newArrayList(behaviours);
    }

    @Override
    public void initialize(Robot robot) {

    }

    @Override
    public void goToPosition(int percentage) {

    }

    @Override
    public void goToDefault() {

    }

    @Override
    public void goToMinimum() {

    }

    @Override
    public void goToMaximum() {

    }
}
