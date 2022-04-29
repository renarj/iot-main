package com.oberasoftware.robo.hcontrol;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InputScaleTest {
    @Test
    public void testScaling() {
        assertThat(InputScaler.convert(2.55), is(0));
        assertThat(InputScaler.convert(1.3), is(-100));
        assertThat(InputScaler.convert(2.0), is(-46));
        assertThat(InputScaler.convert(3.9), is(100));
        assertThat(InputScaler.convert(3.0), is(30));

        assertThat(InputScaler.convert(2.601579393902402), is(0));


        assertThat(InputScaler.convert(1.3580414441358686), is(-92));

    }
}
