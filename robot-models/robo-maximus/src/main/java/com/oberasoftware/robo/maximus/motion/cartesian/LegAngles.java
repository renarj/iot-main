package com.oberasoftware.robo.maximus.motion.cartesian;

import com.oberasoftware.iot.core.robotics.humanoid.joints.Joint;
import com.oberasoftware.iot.core.robotics.motion.KeyFrame;
import com.oberasoftware.robo.core.motion.JointTargetImpl;
import com.oberasoftware.robo.core.motion.KeyFrameImpl;

import java.util.*;

public class LegAngles {
    private final Map<String, Double> angles = new HashMap<>();

    public void addAngleByType(String type, Double angle) {
        this.angles.put(type, angle);
    }

    public double getDrift(String type, Double angle) {
        if (angles.containsKey(type)) {
            return (angles.get(type) / angle) - 1;
        }
        return 0;
    }

    public Set<String> getJointTypes() {
        return new HashSet<>(angles.keySet());
    }

    public double getAngle(String type) {
        return this.angles.get(type);
    }

    public boolean contains(String type) {
        return angles.containsKey(type);
    }

    public KeyFrame toKeyFrame(Map<String, List<Joint>> jointTypeMap, long timeInMs) {
        KeyFrameImpl kf = new KeyFrameImpl(UUID.randomUUID().toString(), timeInMs);
        angles.forEach((key, value) -> {
            jointTypeMap.get(key).forEach(joint -> {
                kf.addServoStep(new JointTargetImpl(joint.getJointId(), 0, value.intValue()));
            });
        });
        return kf;
    }

    @Override
    public String toString() {
        return "LegAngles{" +
                "angles=" + angles +
                '}';
    }
}
