package com.oberasoftware.robo.hexapod;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private final List<Leg> legs;

    private final PosData posData;

    public Frame(List<Leg> legs, PosData posData) {
        this.legs = legs;
        this.posData = posData;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public PosData getPosData() {
        return posData;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "legs=" + legs +
                ", posData=" + posData +
                '}';
    }
}
