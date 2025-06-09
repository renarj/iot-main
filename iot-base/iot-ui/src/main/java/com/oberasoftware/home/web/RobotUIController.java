package com.oberasoftware.home.web;

import com.oberasoftware.iot.core.exceptions.IOTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/robots")
public class RobotUIController {
    private static final Logger LOG = LoggerFactory.getLogger(RobotUIController.class);

    @RequestMapping
    public String getRobots() throws IOTException {
        LOG.debug("Showing Robot Admin screen - controllers");

        return "robots/index";
    }

    @RequestMapping("/editor")
    public String getMotionEditor() throws IOTException {
        LOG.debug("Showing Robot Motion Editor screen - controllers");

        return "robots/editor";
    }

    @RequestMapping("/editor/controllers({controllerId})")
    public String getMotionEditor(@PathVariable String controllerId, Model model) throws IOTException {
        LOG.debug("Showing Robot Motion Editor screen - controller selected: {}", controllerId);
        model.addAttribute("controllerId", controllerId);

        return "robots/editor";
    }

    @RequestMapping("/editor/controllers({controllerId})/robots({robotId})")
    public String getMotionEditor(@PathVariable String controllerId, @PathVariable String robotId, Model model) throws IOTException {
        LOG.debug("Showing Robot Motion Editor screen - controller selected: {} and robot: {}", controllerId, robotId);
        model.addAttribute("controllerId", controllerId);
        model.addAttribute("robotId", robotId);

        return "robots/editor";
    }

    @RequestMapping("/jointcontrol")
    public String getJointControl() throws IOTException {
        LOG.debug("Showing Robot Joint Control screen - controllers");

        return "robots/jointcontrol";
    }

    @RequestMapping("/jointcontrol/controllers({controllerId})")
    public String getJointControl(@PathVariable String controllerId, Model model) throws IOTException {
        LOG.debug("Showing Robot Joint Control screen - controller selected: {}", controllerId);
        model.addAttribute("controllerId", controllerId);

        return "robots/jointcontrol";
    }

    @RequestMapping("/jointcontrol/controllers({controllerId})/robots({robotId})")
    public String getJointControl(@PathVariable String controllerId, @PathVariable String robotId, Model model) throws IOTException {
        LOG.debug("Showing Robot Joint Control screen - controller selected: {} and robot: {}", controllerId, robotId);
        model.addAttribute("controllerId", controllerId);
        model.addAttribute("robotId", robotId);

        return "robots/jointcontrol";
    }
}
