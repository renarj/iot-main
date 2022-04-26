package com.oberasoftware.robo.api.humanoid.cartesian;

import com.oberasoftware.robo.api.behavioural.Behaviour;

import java.util.concurrent.TimeUnit;

public interface CartesianControl extends Behaviour {
    enum EFFECTED_PART {
        TORSO,
        WHOLE_BODY
    }

    boolean move(CartesianMoveInput input);

    boolean setCoordinates(EFFECTED_PART part, double x, double y, double z, long time, TimeUnit unit);

    boolean move(EFFECTED_PART part, double x, double y, double z, long time, TimeUnit unit);

    Coordinates getCurrentCoordinates();
}
