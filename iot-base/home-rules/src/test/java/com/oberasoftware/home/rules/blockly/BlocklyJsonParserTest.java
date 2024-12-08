package com.oberasoftware.home.rules.blockly;

import com.oberasoftware.home.rules.api.Operator;
import com.oberasoftware.home.rules.api.logic.CompareCondition;
import com.oberasoftware.home.rules.api.logic.IfStatement;
import com.oberasoftware.home.rules.blockly.blocks.*;
import com.oberasoftware.home.rules.blockly.blocks.logic.LogicCompareParser;
import com.oberasoftware.home.rules.blockly.blocks.triggers.DayTimeTriggerParser;
import com.oberasoftware.home.rules.blockly.blocks.triggers.SystemTriggerParser;
import com.oberasoftware.home.rules.blockly.blocks.triggers.ThingTriggerParser;
import com.oberasoftware.home.rules.blockly.blocks.values.GetThingParser;
import com.oberasoftware.home.rules.blockly.blocks.values.OnOffParser;
import com.oberasoftware.home.rules.blockly.blocks.values.StaticNumberParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.oberasoftware.home.rules.blocklyv1.BlocklyHelper.parseJsonRule;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BlocklyJsonParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(BlocklyJsonParserTest.class);

    private static final List<BlockParser<?>> evaluators = new ArrayList<>();
    static {
        evaluators.add(new RuleBlockParser());
        evaluators.add(new DayTimeTriggerParser());
        evaluators.add(new SystemTriggerParser());
        evaluators.add(new SetThingStateParser());
        evaluators.add(new ThingTriggerParser());
        evaluators.add(new IfBlockParser());
        evaluators.add(new LogicCompareParser());
        evaluators.add(new GetThingParser());
        evaluators.add(new StaticNumberParser());
        evaluators.add(new SwitchItemParser());
        evaluators.add(new MotionParser());
        evaluators.add(new SetThingStateParser());
        evaluators.add(new OnOffParser());
    }

    @Test
    public void testParseSimpleJson() throws BlocklyParseException, IOException {
        var parsedJson = parseJsonRule(createParser(), "/json/simple.json");
        assertThat(parsedJson, notNullValue());
        LOG.info("Loaded json rule: {}", parsedJson);
        assertThat(parsedJson.getName(), is("testRule"));
        assertThat(parsedJson.getBlocks(), notNullValue());
        assertThat(parsedJson.getTriggers(), notNullValue());
        assertThat(parsedJson.getTriggers().size(), is(1));
        assertThat(parsedJson.getBlocks(), notNullValue());
        assertThat(parsedJson.getBlocks().size(), is(1));
    }

    @Test
    public void testParseIfElseJson() throws BlocklyParseException, IOException {
        var parsedJson = parseJsonRule(createParser(), "/json/simple_ifelse_rule.json");
        assertThat(parsedJson, notNullValue());
        LOG.info("Loaded json rule: {}", parsedJson);
        assertThat(parsedJson.getName(), is("switchPowerOnOff"));
        assertThat(parsedJson.getBlocks(), notNullValue());
        assertThat(parsedJson.getBlocks().size(), is(1));
        assertThat(parsedJson.getTriggers(), notNullValue());
        assertThat(parsedJson.getTriggers().size(), is(2));

        IfStatement ifStatement = (IfStatement) parsedJson.getBlocks().get(0);
        assertThat(ifStatement, notNullValue());
        assertThat(ifStatement.getBranches().size(), is(3));

        var firstBranch = ifStatement.getBranches().get(0);
        var secondBranch = ifStatement.getBranches().get(1);
        assertThat(firstBranch, notNullValue());
        assertThat(secondBranch, notNullValue());


        assertThat(firstBranch.getCondition(), notNullValue());
        assertThat(firstBranch.getCondition(), instanceOf(CompareCondition.class));
        var firstCompare = (CompareCondition) firstBranch.getCondition();
        assertThat(firstCompare.getOperator(), is(Operator.LARGER_THAN));

        assertThat(secondBranch.getCondition(), notNullValue());
        assertThat(secondBranch.getCondition(), instanceOf(CompareCondition.class));
        var secondCompare = (CompareCondition) secondBranch.getCondition();
        assertThat(secondCompare.getOperator(), is(Operator.EQUALS));
    }

    @Test
    public void testMultipleDos() throws BlocklyParseException, IOException {
        var parsedJson = parseJsonRule(createParser(), "/json/multi-dostatements.json");
        assertThat(parsedJson, notNullValue());
        LOG.info("Loaded json rule: {}", parsedJson);
        assertThat(parsedJson.getName(), is("setDegrees"));
        assertThat(parsedJson.getBlocks(), notNullValue());
        assertThat(parsedJson.getTriggers(), notNullValue());
        assertThat(parsedJson.getTriggers().size(), is(1));
    }

    @Test
    public void testParseTimeTrigger() throws BlocklyParseException, IOException {
        var parsedJson = parseJsonRule(createParser(), "/json/datetimeTrigger.json");
        assertThat(parsedJson, notNullValue());
        LOG.info("Loaded json rule: {}", parsedJson);
        assertThat(parsedJson.getName(), is("resetKneeJoint"));
        assertThat(parsedJson.getTriggers(), notNullValue());
        assertThat(parsedJson.getTriggers().size(), is(2));
    }

    @Test
    public void testParseMotionActivate() throws BlocklyParseException, IOException {
        var parsedJson = parseJsonRule(createParser(), "/json/motion-activate.json");
        assertThat(parsedJson, notNullValue());
        LOG.info("Loaded json rule: {}", parsedJson);
        assertThat(parsedJson.getName(), is("armUpWhenGripperClosed"));
        assertThat(parsedJson.getTriggers(), notNullValue());
        assertThat(parsedJson.getTriggers().size(), is(1));
    }

    private BlocklyJsonParser createParser() {
        return new BlocklyJsonParser(new BlockFactory(evaluators));
    }
}
