package com.oberasoftware.robodog.model;

import com.oberasoftware.robodog.odrive.device.ODriveDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODriveJoint implements Joint {
    private static final Logger LOG = LoggerFactory.getLogger(ODriveJoint.class);

    private final String name;
    private final ODriveDevice drive;
    private final boolean reversed;
    private final double minAngle;
    private final double maxAngle;
    private final double centerAngle;

    public ODriveJoint(String name, ODriveDevice drive, boolean reversed, double minAngle, double maxAngle, double centerAngle) {
        this.name = name;
        this.drive = drive;
        this.reversed = reversed;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.centerAngle = centerAngle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void moveToAngle(double degrees) {
        if (degrees < minAngle || degrees > maxAngle) {
            LOG.warn("Requested angle {} for joint {} is out of bounds [{}, {}]", degrees, name, minAngle, maxAngle);
            degrees = Math.max(minAngle, Math.min(maxAngle, degrees));
        }

        double targetDegrees = reversed ? centerAngle - degrees : degrees - centerAngle;
        
        // ODrive native position is in rotations (360 degrees = 1 rotation)
        float odrivePosition = (float) (targetDegrees / 36.0);
        
        LOG.debug("Moving joint {} to {} degrees (ODrive position: {})", name, degrees, odrivePosition);
        drive.moveToPosition(odrivePosition);
    }

    @Override
    public double getCurrentAngle() {
        double odrivePosition = drive.readPosition();
        double degreesFromCenter = odrivePosition * 360.0;
        
        return reversed ? centerAngle - degreesFromCenter : centerAngle + degreesFromCenter;
    }

    public ODriveDevice getDrive() {
        return drive;
    }
}
