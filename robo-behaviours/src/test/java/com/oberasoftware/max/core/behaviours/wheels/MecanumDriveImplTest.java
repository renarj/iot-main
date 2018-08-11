package com.oberasoftware.max.core.behaviours.wheels;

import com.oberasoftware.max.core.behaviours.wheels.impl.MecanumDriveTrainImpl;
import com.oberasoftware.robo.api.behavioural.Wheel;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author renarj
 */
public class MecanumDriveImplTest {
    @Test
    public void testSimpleDrive() {
        Wheel leftFront = mock(Wheel.class);
        Wheel leftRear = mock(Wheel.class);
        Wheel rightFront = mock(Wheel.class);
        Wheel rightRear = mock(Wheel.class);


        MecanumDriveTrainImpl drive = new MecanumDriveTrainImpl(leftFront, rightFront, leftRear, rightRear);
        drive.cartesian(0, -1, 0);
    }
}
