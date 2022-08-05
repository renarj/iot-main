package com.oberasoftware.iot.core.robotics.humanoid;

import com.oberasoftware.iot.core.robotics.MotionTask;
import com.oberasoftware.iot.core.robotics.behavioural.Behaviour;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.motion.Motion;

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
