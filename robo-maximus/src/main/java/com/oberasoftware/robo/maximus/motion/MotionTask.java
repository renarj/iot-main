package com.oberasoftware.robo.maximus.motion;

import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.motion.MotionUnit;

public class MotionTask {
    private final MotionUnit motion;
    private final String taskId;

    public MotionTask(String taskId, Motion motion) {
        this.motion = motion;
        this.taskId = taskId;
    }

    public void markComplete() {

    }

    public MotionUnit getMotion() {
        return motion;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean hasCompleted() {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean hasStarted() {
        return false;
    }
}
