package com.oberasoftware.home.rules.evaluators.values;

import com.oberasoftware.home.rules.api.values.ResolvableValue;
import com.oberasoftware.home.rules.evaluators.Evaluator;
import com.oberasoftware.iot.core.model.Value;

/**
 * @author Renze de Vries
 */
public interface ValueEvaluator<T extends ResolvableValue> extends Evaluator<T, Value> {

}
