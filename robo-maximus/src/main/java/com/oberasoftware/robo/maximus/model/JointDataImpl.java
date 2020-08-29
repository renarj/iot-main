package com.oberasoftware.robo.maximus.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oberasoftware.base.event.Event;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;

import java.util.Map;
import java.util.Set;

public class JointDataImpl implements JointData {
    public static final String JOINTS_PATH_BASE = "joints.";
    public static final String DEGREES = "degrees";
    public static final String POSITION = "position";
    private String id;
    private int degrees;
    private int position;

    public JointDataImpl(String id, int degrees, int position) {
        this.id = id;
        this.position = position;
        this.degrees = degrees;
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
    public Map<String, ?> getValues() {
        return ImmutableMap.<String, Object> builder()
                .put(DEGREES, getDegrees())
                .put(POSITION, getPosition())
                .build();
    }

    @Override
    public <T> T getValue(String attribute) {
        if(DEGREES.equalsIgnoreCase(attribute)) {
            return (T) Integer.valueOf(getDegrees());
        } else if(POSITION.equalsIgnoreCase(attribute)) {
            return (T) Integer.valueOf(getPosition());
        }

        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getDegrees() {
        return degrees;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "JointDataImpl{" +
                "id='" + id + '\'' +
                ", degrees=" + degrees +
                ", position=" + position +
                '}';
    }
}
