package com.oberasoftware.robo.api.humanoid;

import com.oberasoftware.robo.api.MotionTask;
import com.oberasoftware.robo.api.behavioural.Behaviour;

public interface NavigationControl extends Behaviour {
    /**
     * Move the robot into the direction specified
     *
     * @param x Move left/right by x meters
     * @param y Move forward/back by x meters
     * @param z Move around axis by x degrees
     */
    MotionTask move(double x, double y, double z);

    /**
     * Blocking tasks to request robot stop moving safely in a graceful way
     */
    void stopMove();

    /**
     * Direct stop of movement, no safety stop taken
     */
    void unsafeStop();
}
