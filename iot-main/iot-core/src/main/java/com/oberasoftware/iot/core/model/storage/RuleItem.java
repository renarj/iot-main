package com.oberasoftware.iot.core.model.storage;

/**
 * @author Renze de Vries
 */
public interface RuleItem extends PropertiesContainer {
    String getName();

    String getControllerId();

    String getRule();
}
