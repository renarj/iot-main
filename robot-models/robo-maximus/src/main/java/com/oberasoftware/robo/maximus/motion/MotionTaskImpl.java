package com.oberasoftware.robo.maximus.motion;

import com.oberasoftware.robo.api.MotionTask;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.motion.MotionUnit;

public class MotionTaskImpl implements MotionTask {
    public static final int DEFAULT_MAX_LOOP = 1;



    private final MotionUnit motion;
    private final String taskId;
    private final boolean loop;

    private STATE state = STATE.NOT_STARTED;

    private final int maxLoop;

    public MotionTaskImpl(String taskId, Motion motion) {
        this.motion = motion;
        this.taskId = taskId;
        this.loop = false;
        this.maxLoop = DEFAULT_MAX_LOOP;
    }

    public MotionTaskImpl(MotionUnit motion, String taskId, boolean loop, int maxLoop) {
        this.motion = motion;
        this.taskId = taskId;
        this.loop = loop;
        this.maxLoop = maxLoop;
    }

    public void markComplete() {
        this.state = STATE.FINISHED;
    }

    public Motion getMotion() {
        return (Motion) motion;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public boolean hasCompleted() {
        return state == STATE.FINISHED;
    }

    public boolean isCancelled() {
        return state == STATE.CANCELLED;
    }

    public boolean hasStarted() {
        return state == STATE.STARTED || state == STATE.RUNNING;
    }

    public STATE getState() {
        return state;
    }

    public boolean isLoop() {
        return loop;
    }

    public int getMaxLoop() {
        return maxLoop;
    }
}
