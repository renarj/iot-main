package com.oberasoftware.robo.cloud.motion.controllers;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.impl.CommandType;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.motion.controller.HandsController;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.commands.BasicCommandBuilder.create;


/**
 * @author Renze de Vries
 */
@Component
public class RemoteHandsController implements HandsController, RemoteController {

    private RobotHardware robot;

    @Override
    public void openHands() {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .attribute("hands")
                .property("position", "open")
                .build();

        robot.getRemoteDriver().publish(command);
    }

    @Override
    public void openHand(HAND_ID hand) {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .attribute("hands")
                .property("position", "open")
                .property("hand", hand.name())
                .build();

        robot.getRemoteDriver().publish(command);

    }

    @Override
    public void closeHand(HAND_ID hand) {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .attribute("hands")
                .property("position", "closed")
                .property("hand", hand.name())
                .build();

        robot.getRemoteDriver().publish(command);
    }

    @Override
    public void closeHands() {
        BasicCommand command = create(robot.getName())
                .thing("motion")
                .type(CommandType.SET_STATE)
                .attribute("hands")
                .property("position", "closed")
                .build();

        robot.getRemoteDriver().publish(command);
    }

    @Override
    public void activate(RobotHardware robot) {
        this.robot = robot;
    }
}
