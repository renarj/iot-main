package com.oberasoftware.home.rules.blocklyv1.general;

import com.google.common.collect.Lists;
import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.api.trigger.Trigger;
import com.oberasoftware.home.rules.blocklyv1.XMLBlockParser;
import com.oberasoftware.home.rules.blocklyv1.BlockParserFactory;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blocklyv1.XMLUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.oberasoftware.home.rules.blocklyv1.XMLConstants.NAME_ATTRIBUTE;
import static com.oberasoftware.home.rules.blocklyv1.XMLConstants.RULE_NAME;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findElementWithAttribute;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFieldElement;
import static com.oberasoftware.home.rules.blocklyv1.XMLUtils.findFirstBlock;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * @author Renze de Vries
 */
@Component
public class RuleXMLBlockParser implements XMLBlockParser<Rule> {
    private static final Logger LOG = getLogger(RuleXMLBlockParser.class);

    @Autowired
    private BlockParserFactory blockParserFactory;

    @Override
    public boolean supportsType(String type) {
        return type.equals("rule");
    }

    @Override
    public Rule parse(Element node) throws BlocklyParseException {
        Optional<Element> ruleNameElement = findFieldElement(node, RULE_NAME);
        if(ruleNameElement.isPresent()) {
            String ruleName = ruleNameElement.get().getTextContent();

            LOG.debug("Determined rule name: {}", ruleName);

            Element triggerElement = findElementWithAttribute(node, "statement", NAME_ATTRIBUTE, "ruleTrigger")
                    .orElseThrow(() -> new BlocklyParseException("No trigger was defined for rule"));
            List<Trigger> triggers = getTriggers(triggerElement);

            LOG.debug("We have found: {} triggers: {}", triggers.size(), triggers);

            Element statementElement = findElementWithAttribute(node, "statement", NAME_ATTRIBUTE, "ruleStatement")
                    .orElseThrow(() -> new BlocklyParseException("Missing rule statement"));


            Element blockElement = findFirstBlock(statementElement).orElseThrow(() -> new BlocklyParseException("No statements found for rule"));
            Statement statement = getBlock(blockElement);

            return new Rule(null, ruleName, Lists.newArrayList(statement), triggers);
        }

        return null;

    }

    private List<Trigger> getTriggers(Element triggerElement) throws BlocklyParseException {
        List<Trigger> triggers = new ArrayList<>();

        Element triggerBlock = findFirstBlock(triggerElement)
                .orElseThrow(() -> new BlocklyParseException("No trigger block specified"));
        String triggerType = triggerBlock.getAttribute("type");

        XMLBlockParser<Trigger> triggerBlockParser = blockParserFactory.getParser(triggerType);
        Trigger trigger = triggerBlockParser.parse(triggerBlock);
        triggers.add(trigger);

        Optional<Element> nextTrigger = XMLUtils.findFirstElement(triggerBlock, "next");
        if(nextTrigger.isPresent()) {
            triggers.addAll(getTriggers(nextTrigger.get()));
        }
        return triggers;
    }

    private Statement getBlock(Element blockElement) throws BlocklyParseException{
        String blockType = blockElement.getAttribute("type");

        XMLBlockParser<Statement> blockBlockParser = blockParserFactory.getParser(blockType);
        return blockBlockParser.parse(blockElement);
    }
}
