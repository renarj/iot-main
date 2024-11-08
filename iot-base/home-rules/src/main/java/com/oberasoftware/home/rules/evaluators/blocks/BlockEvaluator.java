package com.oberasoftware.home.rules.evaluators.blocks;

import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.evaluators.Evaluator;

/**
 * @author Renze de Vries
 */
public interface BlockEvaluator<T extends Statement> extends Evaluator<T, Boolean> {

}
