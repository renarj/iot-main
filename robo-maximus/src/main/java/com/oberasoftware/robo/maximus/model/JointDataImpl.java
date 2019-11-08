package com.oberasoftware.robo.maximus.model;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.robo.api.behavioural.humanoid.JointData;

public class JointDataImpl implements JointData, Event {
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
