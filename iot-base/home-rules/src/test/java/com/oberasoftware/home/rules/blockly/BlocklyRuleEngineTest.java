package com.oberasoftware.home.rules.blockly;

import com.google.common.collect.Iterables;
import com.oberasoftware.base.event.Event;
import com.oberasoftware.home.rules.RuleConfiguration;
import com.oberasoftware.home.rules.RuleEngine;
import com.oberasoftware.home.rules.TestConfiguration;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.test.MockAutomationBus;
import com.oberasoftware.home.rules.test.MockStateManager;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.commands.impl.SwitchCommandImpl;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.iot.core.legacymodel.impl.StateImpl;
import com.oberasoftware.iot.core.legacymodel.impl.StateItemImpl;
import com.oberasoftware.iot.core.legacymodel.impl.ValueImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.oberasoftware.home.rules.blockly.BlocklyHelper.parseRule;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Renze de Vries
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RuleConfiguration.class, TestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlocklyRuleEngineTest {
    private static final String LUMINANCE_LABEL = "luminance";

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private MockAutomationBus mockAutomationBus;

    @Autowired
    private MockStateManager mockStateManager;

    @Autowired
    private BlocklyParser blocklyParser;

    @Test
    public void testRuleLuminanceConditionTrue() throws Exception {
        Rule rule = parseRule(blocklyParser, "/simple_luminance_rule.xml");
        String ruleId = UUID.randomUUID().toString();
        rule.setId(ruleId);

        StateImpl itemState = new StateImpl("75f1c663-b7df-4036-8a2d-5e3d95f6a596");
        itemState.updateIfChanged(LUMINANCE_LABEL, new StateItemImpl(LUMINANCE_LABEL, new ValueImpl(VALUE_TYPE.NUMBER, 1l)));
        mockStateManager.addState(itemState);


        ruleEngine.register(rule);

        ruleEngine.evalRule(ruleId);

        List<Event> publishedEvents = mockAutomationBus.getPublishedEvents();
        assertThat(publishedEvents.size(), is(1));
        Event event = publishedEvents.get(0);
        String targetDevice = "0000001e-0661-7a39-0000-014e5fa2231e";
        assertThat(event, is(new ItemCommandEvent(targetDevice, new SwitchCommandImpl("controllerId", targetDevice, SwitchCommand.STATE.ON))));
    }

    @Test
    public void testRuleLuminanceConditionFalse() throws Exception {
        Rule rule = parseRule(blocklyParser, "/simple_luminance_rule.xml");
        String ruleId = UUID.randomUUID().toString();
        rule.setId(ruleId);

        StateImpl itemState = new StateImpl("75f1c663-b7df-4036-8a2d-5e3d95f6a596");
        itemState.updateIfChanged(LUMINANCE_LABEL, new StateItemImpl(LUMINANCE_LABEL, new ValueImpl(VALUE_TYPE.NUMBER, 20l)));
        mockStateManager.addState(itemState);


        ruleEngine.register(rule);

        ruleEngine.evalRule(ruleId);

        List<Event> publishedEvents = mockAutomationBus.getPublishedEvents();
        assertThat(publishedEvents.size(), is(0));
    }


    @Test
    public void testRuleSetItemValue() throws Exception {
        Rule rule = parseRule(blocklyParser, "/update_power_start.xml");
        String ruleId = UUID.randomUUID().toString();
        rule.setId(ruleId);

        StateImpl itemState = new StateImpl("6d1a20a5-7347-41cf-bdc7-4f6df2035b24");
        itemState.updateIfChanged("KWH", new StateItemImpl("KWH", new ValueImpl(VALUE_TYPE.NUMBER, 12345)));
        mockStateManager.addState(itemState);

        ruleEngine.register(rule);

        ruleEngine.evalRule(ruleId);

        List<Event> publishedEvents = mockAutomationBus.getPublishedEvents();
        assertThat(publishedEvents.size(), is(1));

        Map<String, Value> values = new HashMap<>();
        values.put("PowerStart", new ValueImpl(VALUE_TYPE.NUMBER, 12345));
        ItemCommand command = new ValueCommandImpl("controllerId", "6d1a20a5-7347-41cf-bdc7-4f6df2035b24", values);
        ItemCommandEvent commandEvent = new ItemCommandEvent("6d1a20a5-7347-41cf-bdc7-4f6df2035b24", command);

        assertThat(Iterables.getFirst(publishedEvents, null), is(commandEvent));
    }

    @Test
    public void testStateMathOperation() throws Exception {
        Rule rule = parseRule(blocklyParser, "/simple_math.xml");
        String ruleId = UUID.randomUUID().toString();
        rule.setId(ruleId);

        String itemId = "6d1a20a5-7347-41cf-bdc7-4f6df2035b24";
        StateImpl itemState = new StateImpl(itemId);
        itemState.updateIfChanged("energy", new StateItemImpl("energy", new ValueImpl(VALUE_TYPE.NUMBER, 100)));
        itemState.updateIfChanged("PowerStart", new StateItemImpl("PowerStart", new ValueImpl(VALUE_TYPE.NUMBER, 55)));
        mockStateManager.addState(itemState);

        ruleEngine.register(rule);

        ruleEngine.evalRule(ruleId);

        List<Event> publishedEvents = mockAutomationBus.getPublishedEvents();
        assertThat(publishedEvents.size(), is(1));
        Map<String, Value> values = new HashMap<>();
        values.put("PowerUsed", new ValueImpl(VALUE_TYPE.NUMBER, 45l));
        ItemCommand command = new ValueCommandImpl("controllerId", itemId, values);
        ItemCommandEvent commandEvent = new ItemCommandEvent(itemId, command);

        assertThat(Iterables.getFirst(publishedEvents, null), is(commandEvent));

    }

}
