package com.oberasoftware.robomax.core;

import com.oberasoftware.robo.api.motion.Motion;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Renze de Vries
 */
public class JSonMotionLoaderTest {
    @Test
    public void testLoadJsonMotion() {
        List<Motion> motions = new JSonMotionLoader().loadMotions("/test-motion.json");
        assertThat(motions.size(), is(1));

        Motion motion = motions.get(0);
        assertThat(motion.getName(), is("Turn right"));
    }
}
