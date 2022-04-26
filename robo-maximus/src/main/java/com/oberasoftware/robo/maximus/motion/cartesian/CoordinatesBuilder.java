package com.oberasoftware.robo.maximus.motion.cartesian;

import com.oberasoftware.robo.api.humanoid.cartesian.Coordinates;
import com.oberasoftware.robo.api.humanoid.cartesian.CoordinatesImpl;

public class CoordinatesBuilder {
    private double x;
    private double y;
    private double z;

    public CoordinatesBuilder(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordinatesBuilder applyXMinMax(double min, double max) {
        if(x <= min) {
            x = min;
        } else if (x >= max) {
            x = max;
        }
        return this;
    }

    public CoordinatesBuilder applyYMinMax(double min, double max) {
        if(y <= min) {
            y = min;
        } else if(x >= max) {
            x = max;
        }
        return this;
    }

    public CoordinatesBuilder applyZMinMax(double min, double max) {
        if(z <= min) {
            z = min;
        } else if(z >= max) {
            z = max;
        }
        return this;
    }

    public Coordinates build() {
        return new CoordinatesImpl(x, y, z);
    }
}
