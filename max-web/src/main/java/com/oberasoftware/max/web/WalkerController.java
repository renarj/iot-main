package com.oberasoftware.max.web;

import com.oberasoftware.robo.hexapod.PosData;
import com.oberasoftware.robo.hexapod.RobotFeedback;
import com.oberasoftware.robo.hexapod.Walker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnBean(Walker.class)
public class WalkerController {
    private static final Logger LOG = LoggerFactory.getLogger(WalkerController.class);

    @Autowired
    private Walker walker;

    @RequestMapping(value = "/walker/{dirX}/{dirY}/{dirZ}/{rotX}/{rotY}/{rotZ}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public RobotFeedback walk(@PathVariable Double dirX, @PathVariable Double dirY, @PathVariable Double dirZ,
                              @PathVariable Double rotX, @PathVariable Double rotY, @PathVariable Double rotZ) {
        LOG.info("Received walker input Direction {}/{}/{} Rotation: {}/{}/{}", dirX, dirY, dirZ, rotX, rotY, rotZ);

        return walker.walk(new PosData(dirX, dirY, dirZ), new PosData(rotX, rotY, rotZ));
    }

    @RequestMapping(value = "/walker/leg/{legName}/{dirX}/{dirY}/{dirZ}/{rotX}/{rotY}/{rotZ}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public RobotFeedback leg(@PathVariable String legName, @PathVariable Double dirX, @PathVariable Double dirY, @PathVariable Double dirZ,
                              @PathVariable Double rotX, @PathVariable Double rotY, @PathVariable Double rotZ) {
        LOG.info("Received walker input for leg: {} Direction {}/{}/{} Rotation: {}/{}/{}", legName, dirX, dirY, dirZ, rotX, rotY, rotZ);

        return walker.setLeg(legName, new PosData(dirX, dirY, dirZ), new PosData(rotX, rotY, rotZ));
    }

}
