package com.oberasoftware.home.rules.evaluators.values;

import com.oberasoftware.home.rules.api.values.StaticValue;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Renze de Vries
 */
@Component
public class StaticValueEvaluator implements ValueEvaluator<StaticValue> {
    @Override
    public Value eval(StaticValue input) {
        return new ValueImpl(input.getType(), input.getValue());
    }

    @Override
    public Set<String> getDependentItems(StaticValue input) {
        return new HashSet<>();
    }
}
