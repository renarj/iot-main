package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.api.trigger.Trigger;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class RuleBlockParser implements BlockParser<Rule> {
    private static final Logger LOG = LoggerFactory.getLogger(RuleBlockParser.class);

    @Override
    public boolean supportsType(String type) {
        return "rule".equalsIgnoreCase(type);
    }

    @Override
    public Rule transform(final BlockFactory blockFactory, BlocklyObject block) throws BlocklyParseException{
        LOG.info("Evaluating Rule block: {}", block);
        String ruleName = block.getFields().get("rule_name");
        if(ruleName != null) {
            BlockUtils.assertFieldPresent(block, "ruleStatement", "No statements specified for rule");
            BlockUtils.assertFieldPresent(block, "ruleTrigger", "No triggers specified for rule");
            var triggers = getTriggers(blockFactory, block);
            var statements = getStatements(blockFactory, block);

            if(triggers.isEmpty()) {
                throw new BlocklyParseException("No rule triggers found");
            }
            if(statements.isEmpty()) {
                throw new BlocklyParseException("No rule statements found");
            }
            return new Rule(block.getId(), ruleName, statements, triggers);
        } else {
            throw new BlocklyParseException("No rule name defined for rule block");
        }
    }

    private List<Statement> getStatements(BlockFactory blockFactory, BlocklyObject block) {
        var inputField = block.getInputs().get("ruleStatement");
        if(inputField!=null) {
            var statements = BlockUtils.getChainAsList(inputField.getBlock());
            LOG.info("Found statement blocks: {}", statements);

            var transformedStatements = statements.stream()
                    .map(s -> (Statement) blockFactory.getParser(s.getType()).transform(blockFactory, s))
                    .filter(Objects::nonNull).toList();
            LOG.info("Transformed statements: {}", transformedStatements);
            return transformedStatements;
        } else {
            throw new BlocklyParseException("No statement defined for rule block");
        }
    }

    private List<Trigger> getTriggers(BlockFactory blockFactory, BlocklyObject block) {
        var inputField = block.getInputs().get("ruleTrigger");
        if(inputField!=null) {
            var triggers = BlockUtils.getChainAsList(inputField.getBlock());
            LOG.info("Found trigger blocks: {}", triggers);

            var transformedTriggers = triggers.stream()
                    .map(t -> (Trigger) blockFactory.getParser(t.getType()).transform(blockFactory, t)).toList();
            LOG.info("Transformed triggers: {}", transformedTriggers);
            return transformedTriggers;
        } else {
            throw new BlocklyParseException("No trigger defined for rule block");
        }
    }
}
