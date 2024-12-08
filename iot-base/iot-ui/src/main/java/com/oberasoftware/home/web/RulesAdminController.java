package com.oberasoftware.home.web;

import com.oberasoftware.iot.core.client.AgentClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Renze de Vries
 */
@Controller
@RequestMapping("/web/admin/rules")
public class RulesAdminController {

    @Autowired
    private AgentClient agentClient;

    @RequestMapping
    public String getRules(Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = agentClient.getControllers();
        model.addAttribute("controllers", controllers);

        return "admin/rules";
    }

    @RequestMapping("/{controllerId}")
    public String getRules(@PathVariable String controllerId, Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = agentClient.getControllers();

        model.addAttribute("controllers", controllers);
        model.addAttribute("selectedController", controllerId);

        return "admin/rules";
    }

    @RequestMapping("/{controllerId}/{ruleId}")
    public String editRule(@PathVariable String controllerId, @PathVariable String ruleId, Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = agentClient.getControllers();

        model.addAttribute("controllers", controllers);
        model.addAttribute("selectedController", controllerId);
        model.addAttribute("selectedRule", ruleId);

        return "admin/rules";
    }
}
