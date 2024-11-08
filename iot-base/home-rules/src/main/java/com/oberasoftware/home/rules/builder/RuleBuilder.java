package com.oberasoftware.home.rules.builder;

import com.google.common.collect.Lists;
import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.api.trigger.DayTimeTrigger;
import com.oberasoftware.home.rules.api.trigger.SystemTrigger;
import com.oberasoftware.home.rules.api.trigger.ThingTrigger;
import com.oberasoftware.home.rules.api.trigger.Trigger;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Renze de Vries
 */
public class RuleBuilder {

    private final Rule rule;

    private final List<Trigger> triggers = new ArrayList<>();

    private final List<BlockBuilder> blockBuilders = new ArrayList<>();

    private RuleBuilder(String ruleName) {
        this.rule = new Rule(null, ruleName, Lists.newArrayList(), null);
    }

    public static RuleBuilder create(String name) {
        return new RuleBuilder(name);
    }

    public RuleBuilder triggerOnDeviceChange() {
        this.triggers.add(new ThingTrigger(ThingTrigger.TRIGGER_TYPE.THING_STATE_CHANGE));

        return this;
    }

    public RuleBuilder triggerAtTime(int hour, int minute) {
        this.triggers.add(new DayTimeTrigger(hour, minute));

        return this;
    }

    public RuleBuilder triggerOnSystemChange() {
        this.triggers.add(new SystemTrigger());

        return this;
    }

    public IfBlockBuilder when(ConditionBuilder conditionBuilder) {
        IfBlockBuilder ifBlockBuilder = new IfBlockBuilder(this, conditionBuilder);
        blockBuilders.add(ifBlockBuilder);

        return ifBlockBuilder;
    }

    public SetStateBlockBuilder setItemState(String controllerId, String itemId, String label) {
        SetStateBlockBuilder setStateBlockBuilder = new SetStateBlockBuilder(this, new ThingAttributeValue(controllerId, itemId, label));

        blockBuilders.add(setStateBlockBuilder);

        return setStateBlockBuilder;
    }

    public Rule build() {
        rule.setTriggers(triggers);

        List<Statement> statements = blockBuilders.stream().map(BlockBuilder::buildBlock).collect(Collectors.toList());
        rule.setBlocks(statements.stream().toList());

        return rule;
    }
}
