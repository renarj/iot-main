package com.oberasoftware.robo.maximus.rest;

import com.oberasoftware.robo.api.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.robo.api.behavioural.humanoid.HumanoidRobot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/humanoid")
public class HumanoidRestService {

    private final BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Autowired
    public HumanoidRestService(BehaviouralRobotRegistry behaviouralRobotRegistry) {
        this.behaviouralRobotRegistry = behaviouralRobotRegistry;
    }

    @RequestMapping
    public List<HumanoidRobot> getRobots() {
        return behaviouralRobotRegistry.getRobots().stream()
                .filter(r -> HumanoidRobot.class.isAssignableFrom(r.getClass()))
                .map(r -> (HumanoidRobot) r).collect(Collectors.toList());
    }
}
