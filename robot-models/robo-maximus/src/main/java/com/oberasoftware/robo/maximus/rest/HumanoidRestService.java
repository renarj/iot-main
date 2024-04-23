package com.oberasoftware.robo.maximus.rest;

import com.oberasoftware.iot.core.robotics.behavioural.Robot;
import com.oberasoftware.iot.core.robotics.behavioural.BehaviouralRobotRegistry;
import com.oberasoftware.iot.core.robotics.humanoid.HumanoidRobot;
import com.oberasoftware.iot.core.robotics.humanoid.joints.JointData;
import com.oberasoftware.iot.core.robotics.motion.JointTarget;
import com.oberasoftware.robo.core.motion.JointTargetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/humanoid/")
public class HumanoidRestService {
    private static final Logger LOG = LoggerFactory.getLogger(HumanoidRestService.class);

    private final BehaviouralRobotRegistry behaviouralRobotRegistry;

    @Autowired
    public HumanoidRestService(BehaviouralRobotRegistry behaviouralRobotRegistry) {
        this.behaviouralRobotRegistry = behaviouralRobotRegistry;
    }

    @RequestMapping
    public List<HumanoidRobot> getRobots() {
        LOG.info("Requesting all humanoid robots");
        return behaviouralRobotRegistry.getRobots().stream()
                .filter(r -> HumanoidRobot.class.isAssignableFrom(r.getClass()))
                .map(r -> (HumanoidRobot) r).collect(Collectors.toList());
    }

    @RequestMapping(path = "/robot/{robotId}")
    public ResponseEntity<HumanoidRobot> getRobot(@PathVariable String robotId) {
        LOG.info("Requesting robot with id: {}", robotId);

        return findRobot(robotId).map(behaviouralRobot -> new ResponseEntity<>(behaviouralRobot, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/robot/{robotId}/joints")
    public ResponseEntity<List<JointData>> getJointData(@PathVariable String robotId) {
        Optional<HumanoidRobot> robot = findRobot(robotId);
        return robot.map(humanoidRobot -> new ResponseEntity<>(humanoidRobot.getJointControl().getJointsData(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/robot/{robotId}/joints/{jointId}")
    public ResponseEntity<JointData> getJointData(@PathVariable String robotId, @PathVariable String jointId) {
        return findRobot(robotId).map(h -> new ResponseEntity<>(h.getJointControl().getJointData(jointId).orElseThrow(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/robot/{robotId}/joints", method = RequestMethod.POST)
    public ResponseEntity<Object> setJointAngles(@PathVariable String robotId, @RequestBody List<JointTargetImpl> jointAngles) {
        Optional<HumanoidRobot> robot = findRobot(robotId);

        LOG.info("Joint angles requested: {}", jointAngles);

        robot.ifPresent(r -> r.getJointControl()
                .setJointPositions(jointAngles.stream().map(j -> (JointTarget)j)
                .collect(Collectors.toList())));

        return robot.map(r -> ResponseEntity.accepted().build())
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/robot/{robotId}/joint", method = RequestMethod.POST)
    public ResponseEntity<Object> setJointAngle(@PathVariable String robotId, @RequestBody JointTargetImpl jointAngle) {
        Optional<HumanoidRobot> robot = findRobot(robotId);

        LOG.info("Joint angle request: {}", jointAngle);

        robot.ifPresent(r -> r.getJointControl().setJointPosition(jointAngle));

        return robot.map(r -> ResponseEntity.accepted().build())
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private Optional<HumanoidRobot> findRobot(String robotId) {
        Optional<Robot> robot = behaviouralRobotRegistry.getRobot(robotId);

        return robot.map(r -> (HumanoidRobot) r);
    }
}

