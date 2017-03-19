package com.oberasoftware.max.core.behaviours.servos;

import com.oberasoftware.max.core.behaviours.Behaviour;

/**
 * @author renarj
 */
public interface ServoBehaviour extends Behaviour {
    void goToPosition(int percentage);

    void goToMinimum();

    void goToMaximum();

    void goToDefault();
}
