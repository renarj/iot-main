package com.oberasoftware.home.rules.evaluators;

/**
 * @author Renze de Vries
 */
public class EvalException extends RuntimeException {
    public EvalException(String message, Throwable e) {
        super(message, e);
    }

    public EvalException(String message) {
        super(message);
    }
}
