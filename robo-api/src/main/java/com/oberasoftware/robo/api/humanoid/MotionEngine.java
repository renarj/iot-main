package com.oberasoftware.robo.api.humanoid;

import com.oberasoftware.robo.api.MotionTask;
import com.oberasoftware.robo.api.behavioural.Behaviour;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.motion.Motion;

import java.util.List;
import java.util.Optional;

public interface MotionEngine extends Behaviour {
    MotionTask post(Motion motion) throws RoboException;

    List<MotionTask> getTasks();

    Optional<MotionTask> getTask(String taskId);

    boolean stop() throws RoboException;

    boolean resume() throws RoboException;

    void resetTasks() throws RoboException;

    boolean removeTask(String taskId) throws RoboException;
}
