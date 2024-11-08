package com.oberasoftware.home.rules.builder;

import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.general.SetState;
import com.oberasoftware.home.rules.api.values.ThingAttributeValue;
import com.oberasoftware.home.rules.api.values.ResolvableValue;

/**
 * @author Renze de Vries
 */
public class SetStateBlockBuilder implements BlockBuilder {

    private final RuleBuilder ruleBuilder;
    private final ThingAttributeValue thingAttributeValue;
    private ResolvableValue sourceValue;

    public SetStateBlockBuilder(RuleBuilder ruleBuilder, ThingAttributeValue thingAttributeValue) {
        this.ruleBuilder = ruleBuilder;
        this.thingAttributeValue = thingAttributeValue;
    }

    public RuleBuilder fromItem(String controllerId, String itemId, String label) {
        sourceValue = new ThingAttributeValue(controllerId, itemId, label);

        return ruleBuilder;
    }

    @Override
    public Statement buildBlock() {
        return new SetState(thingAttributeValue, sourceValue);
    }
}
