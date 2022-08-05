package com.oberasoftware.iot.core.robotics.humanoid.joints;


import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;

import java.util.List;
import java.util.Optional;

public interface JointChain extends Behaviour {
    default List<Joint> getJoints() {
        return getJoints(false);
    }

    List<Joint> getJoints(boolean includeChildren);

    default Optional<Joint> getJoint(String name) {
        return getJoints().stream()
                .filter(j -> j.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    String getName();
}
