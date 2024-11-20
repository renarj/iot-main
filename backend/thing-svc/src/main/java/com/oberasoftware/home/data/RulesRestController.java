package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.managers.RuleManager;
import com.oberasoftware.iot.core.model.storage.RuleItem;
import com.oberasoftware.iot.core.model.storage.impl.RuleItemImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
@RestController
@RequestMapping("/api")
public class RulesRestController {
    private static final Logger LOG = getLogger(RulesRestController.class);

    private static final String BAD_RESPONSE = "{\"error\":500, \"message\": \"%s\"}";

    @Autowired
    private RuleManager ruleManager;

    @RequestMapping("/rules")
    public List<RuleItem> findAllRules() {
        return ruleManager.getRules();
    }

    @RequestMapping(value = "/rules/controller({controllerId})")
    public List<RuleItem> findRules(@PathVariable String controllerId) {
        return ruleManager.getRules(controllerId);
    }

    @RequestMapping(value = "/rules", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createRule(@RequestBody RuleItemImpl item) throws Exception {
        LOG.debug("New Rule posted: {}", item);

        try {
            return ResponseEntity.ok(ruleManager.store(item));
        } catch(RuntimeIOTException e) {
            return ResponseEntity.badRequest().body(String.format(BAD_RESPONSE, e.getMessage()));
        }
    }

    @RequestMapping(value = "/rules({ruleId})", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRule(@PathVariable String ruleId) {
        LOG.debug("Deleting Rule: {}", ruleId);
        ruleManager.delete(ruleId);
    }
}
