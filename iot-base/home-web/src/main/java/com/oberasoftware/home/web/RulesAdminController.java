package com.oberasoftware.home.web;

import com.oberasoftware.iot.core.client.ThingClient;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.managers.RuleManager;
import com.oberasoftware.iot.core.model.storage.RuleItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * @author Renze de Vries
 */
@Controller
@RequestMapping("/web/admin/rules")
public class RulesAdminController {

    @Autowired
    private ThingClient thingClient;

    @Autowired
    private RuleManager ruleManager;

    @RequestMapping
    public String getRules(Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = thingClient.getControllers();
        model.addAttribute("controllers", controllers);

        return "admin/rules";
    }

    @RequestMapping("/{controllerId}")
    public String getRules(@PathVariable String controllerId, Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = thingClient.getControllers();
        List<RuleItem> ruleItems = ruleManager.getRules(controllerId);

        model.addAttribute("controllers", controllers);
        model.addAttribute("selectedController", controllerId);
        model.addAttribute("rules", ruleItems);

        return "admin/rules";
    }

    @RequestMapping("/{controllerId}/{ruleId}")
    public String editRule(@PathVariable String controllerId, @PathVariable String ruleId, Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = thingClient.getControllers();
        List<RuleItem> ruleItems = ruleManager.getRules(controllerId);
        Optional<RuleItem> selectedRule = ruleManager.getRule(ruleId);

        model.addAttribute("controllers", controllers);
        model.addAttribute("selectedController", controllerId);
        if(selectedRule.isPresent()) {
            model.addAttribute("selectedRule", ruleId);
            model.addAttribute("rule", selectedRule.get());
        }
        model.addAttribute("rules", ruleItems);

        return "admin/rules";
    }

    @RequestMapping("/{controllerId}/new")
    public String newRule(@PathVariable String controllerId, Model model) throws IOTException {
        List<com.oberasoftware.iot.core.model.Controller> controllers = thingClient.getControllers();
        List<RuleItem> ruleItems = ruleManager.getRules(controllerId);

        model.addAttribute("controllers", controllers);
        model.addAttribute("selectedController", controllerId);
        model.addAttribute("rules", ruleItems);
        model.addAttribute("newRule", "true");

        return "admin/rules";
    }
}
