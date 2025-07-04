package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointData;
import com.oberasoftware.iot.core.robotics.servo.ServoProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JointDataImpl implements JointData {
    public static final String JOINTS_PATH_BASE = "joints.";
    public static final String DEGREES = "degrees";
    public static final String POSITION = "position";
    public static final String TORGUE = "torgue";

    private String id;
    private String controllerId;
//    private int degrees;
//    private int position;
//    private int torgueState;

    private Map<String, Value> jointValues = new HashMap<>();

    public JointDataImpl(String controllerId, String id) {
        this.id = id;
        this.controllerId = controllerId;
    }

    public JointDataImpl(String controllerId, String id, Map<String, Value> jointValues) {
        this.id = id;
        this.controllerId = controllerId;
        this.jointValues = jointValues;
    }

    public JointDataImpl() {
    }

    @Override
    public String getSourcePath() {
        return JOINTS_PATH_BASE + getId();
    }

    @Override
    public Set<String> getAttributes() {
        return Sets.newHashSet(DEGREES, POSITION);
    }

    @Override
    public Map<String, Value> getValues() {
//        return ImmutableMap.<String, Object> builder()
//                .put(DEGREES, getDegrees())
//                .put(POSITION, getPosition())
//                .put(TORGUE, getTorgueState())
//                .build();

        return new HashMap<>(jointValues);
    }

    @Override
    public <T> T getValue(String attribute) {
        return (T) jointValues.get(attribute).getValue();
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getDegrees() {
        Integer degrees = getValue(DEGREES);
        return degrees == null ? 0 : degrees;
    }

    @Override
    public int getPosition() {
        Integer position = getValue(ServoProperty.POSITION.name().toLowerCase());
        return position == null ? 0 : position;
    }

    @Override
    public int getTorgueState() {
        Integer val = getValue(ServoProperty.TORGUE.name().toLowerCase());
        return val == null ? 0 : val;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public String toString() {
        return "JointDataImpl{" +
                "id='" + id + '\'' +
                ", jointValues=" + jointValues +
                '}';
    }
}
