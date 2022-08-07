package com.oberasoftware.iot.core.robotics.humanoid.components;

public interface Legs extends ChainSet {
    Leg getLeg(String legName);
}
