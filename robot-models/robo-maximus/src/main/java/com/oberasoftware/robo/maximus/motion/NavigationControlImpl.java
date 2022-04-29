package com.oberasoftware.robo.maximus.motion;

import com.oberasoftware.robo.api.MotionTask;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.behavioural.BehaviouralRobot;
import com.oberasoftware.robo.api.humanoid.MotionEngine;
import com.oberasoftware.robo.api.humanoid.NavigationControl;
import com.oberasoftware.robo.api.humanoid.cartesian.CartesianControl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class NavigationControlImpl implements NavigationControl {
    private static final Logger LOG = getLogger( NavigationControlImpl.class );

    private static final double SAFE_STAND_HEIGHT = 303.0;
    private static final double READY_WALK_STANCE_HEIGHT = 280.0;

    private CartesianControl cartesianControl;
    private MotionEngine motionEngine;

    @Override
    public void initialize(BehaviouralRobot behaviouralRobot, Robot robotCore) {
        this.motionEngine = behaviouralRobot.getBehaviour(MotionEngine.class);
        this.cartesianControl = behaviouralRobot.getBehaviour(CartesianControl.class);
    }

    @Override
    public MotionTask move(double x, double y, double z) {
        LOG.info("Moving robot in direction x: {} y: {} z: {}", x, y, z);
        /*
          General plan for moving robot
           1. Bend the knees a bit
           2. Create a loop
           3. In loop bend a bit to one side to aleviate pressure of leg moving
           4. move leg a bit in right direction
           5. Put pressure on leg
           6. straighten legs and repeat from 4
         */

        List<WalkStep> gaitSteps = new ArrayList<>();
        gaitSteps.add(new ReadyWalk());

        var it = gaitSteps.iterator();
        while(it.hasNext()) {
            WalkStep step = it.next();
            NavigationState state = step.getState();
            LOG.info("Executing step: {}", step.name());


            step.executeStep();



            if(!it.hasNext()) {
                var se = step.safeExitStep();
                LOG.info("Final step in the chain, safe exit: {}", se);

                se.ifPresent(WalkStep::executeStep);
            }
        }



        return null;
    }

    @Override
    public void stopMove() {

    }

    @Override
    public void unsafeStop() {

    }

    private class Coordinates {
        private final int x, y, z;

        private Coordinates(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private abstract class WalkStep {
        private final String name;
        private MotionTaskImpl motionTask;
        private NavigationState state;

        protected WalkStep(String name) {
            this.name = name;

            motionTask = new MotionTaskImpl(name, null);
            state = new NavigationState();
        }

        abstract void executeStep();

        abstract Optional<WalkStep> safeExitStep();

        NavigationState getState() {
            return state;
        }

        public String name() {
            return this.name;
        }
    }

    private class ReadyWalk extends WalkStep {
        public ReadyWalk() {
            super("InitWalk");
        }

        @Override
        public void executeStep() {
            cartesianControl.setCoordinates(CartesianControl.EFFECTED_PART.TORSO, 0.0, 0.0, READY_WALK_STANCE_HEIGHT, 5, TimeUnit.SECONDS);
        }

        @Override
        Optional<WalkStep> safeExitStep() {
            return Optional.of(new FinishWalk());
        }
    }

    private class FinishWalk extends WalkStep {
        public FinishWalk() {
            super("FinishWalk");
        }

        @Override
        void executeStep() {
//            cartesianControl.move(CartesianControl.EFFECTED_PART.TORSO, 0.0, 0.0, SAFE_STAND_HEIGHT, 5, TimeUnit.SECONDS);
        }

        @Override
        Optional<WalkStep> safeExitStep() {
            return Optional.empty();
        }
    }

    private class NavigationState implements MotionTask {
        private STATE state = STATE.NOT_STARTED;
        private WalkStep currentStep;

        public WalkStep getCurrentStep() {
            return currentStep;
        }

        public void setCurrentStep(WalkStep currentStep) {
            this.currentStep = currentStep;
        }

        @Override
        public STATE getState() {
            return state;
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

        @Override
        public boolean isLoop() {
            return true;
        }

        @Override
        public int getMaxLoop() {
            return -1;
        }
    }
}
