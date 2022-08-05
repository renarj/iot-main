package com.oberasoftware.iot.core.legacymodel;

/**
 * @author renarj
 */
public interface Value {
    VALUE_TYPE getType();

    <T> T getValue();

    String asString();
}
