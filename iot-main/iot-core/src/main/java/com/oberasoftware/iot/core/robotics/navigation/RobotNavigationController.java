package com.oberasoftware.iot.core.robotics.navigation;


import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;

public interface RobotNavigationController extends Behaviour {
    void move(DirectionalInput input);

    DirectionalInput getNavigationDirections();

    void stop();
}
