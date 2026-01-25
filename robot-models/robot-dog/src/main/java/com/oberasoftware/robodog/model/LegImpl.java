package com.oberasoftware.robodog.model;

public class LegImpl implements Leg {
    private final String name;
    private final Joint kneeJoint;
    private final Joint hipPitchJoint;
    private final Joint hipRollJoint;

    private final double thighLenght;
    private final double shinLength;

    public LegImpl(String name, Joint kneeJoint, Joint hipPitchJoint, Joint hipRollJoint, double thighLenght, double shinLength) {
        this.name = name;
        this.kneeJoint = kneeJoint;
        this.hipPitchJoint = hipPitchJoint;
        this.hipRollJoint = hipRollJoint;
        this.thighLenght = thighLenght;
        this.shinLength = shinLength;
    }

    @Override
    public void moveFootTo(Vector3D position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        // Hip Roll
        double hipRollAngle = Math.toDegrees(Math.atan2(y, z));
        hipRollJoint.moveToAngle(hipRollAngle);

        // Calculate the length of the leg in the X-Z plane after hip roll
        double lengthYZ = Math.sqrt(y * y + z * z);
        
        // Distance from hip pitch joint to foot in X-Z' plane
        double d = Math.sqrt(x * x + lengthYZ * lengthYZ);

        // Law of cosines for the triangle formed by femur, tibia, and d
        // d^2 = femur^2 + tibia^2 - 2*femur*tibia*cos(kneeAngle)
        double cosKnee = (thighLenght * thighLenght + shinLength * shinLength - d * d) / (2 * thighLenght * shinLength);
        cosKnee = Math.max(-1, Math.min(1, cosKnee));
        double kneeAngleRad = Math.acos(cosKnee);
        double kneeAngle = 180 - Math.toDegrees(kneeAngleRad);

        // Hip Pitch
        double alpha = Math.atan2(x, lengthYZ);
        double cosBeta = (thighLenght * thighLenght + d * d - shinLength * shinLength) / (2 * thighLenght * d);
        cosBeta = Math.max(-1, Math.min(1, cosBeta));
        double beta = Math.acos(cosBeta);
        double hipPitchAngle = Math.toDegrees(alpha + beta);

        kneeJoint.moveToAngle(kneeAngle);
        hipPitchJoint.moveToAngle(hipPitchAngle);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Joint getKneeJoint() {
        return kneeJoint;
    }

    @Override
    public Joint getHipPitchJoint() {
        return hipPitchJoint;
    }

    @Override
    public Joint getHipRollJoint() {
        return hipRollJoint;
    }
}
