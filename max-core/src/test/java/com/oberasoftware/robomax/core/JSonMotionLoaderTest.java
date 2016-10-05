package com.oberasoftware.robomax.core;

import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robomax.core.motion.JsonMotionLoader;
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
        List<Motion> motions = new JsonMotionLoader().loadMotions("/test-motion.json");
        assertThat(motions.size(), is(2));

        Motion motion = motions.get(0);
        assertThat(motion.getName(), is("Motion1"));

        motion = motions.get(1);
        assertThat(motion.getName(), is("Turn right"));

    }
}
