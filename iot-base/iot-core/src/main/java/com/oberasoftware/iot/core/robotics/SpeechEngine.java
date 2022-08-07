package com.oberasoftware.iot.core.robotics;

import com.oberasoftware.iot.core.robotics.ActivatableCapability;

/**
 * @author Renze de Vries
 */
public interface SpeechEngine extends ActivatableCapability {
    void say(String text, String language);
}
