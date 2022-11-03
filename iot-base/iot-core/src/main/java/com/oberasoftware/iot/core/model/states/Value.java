package com.oberasoftware.iot.core.model.states;

import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;

/**
 * @author renarj
 */
public interface Value {
    VALUE_TYPE getType();

    <T> T getValue();

    String asString();
}
