package com.oberasoftware.robo.api;

/**
 * @author Renze de Vries
 */
public interface MotionTask {
    enum STATE {
        NOT_STARTED,
        STARTED,
        RUNNING,
        FINISHED,
        CANCELLED
    }

    STATE getState();

    boolean hasCompleted();

    boolean isCancelled();

    boolean hasStarted();

    boolean isLoop();

    int getMaxLoop();
}
