package com.oberasoftware.home.agent.core.storage;

/**
 * @author Renze de Vries
 */
public interface AgentStorage {
    String getValue(String key);

    String getValue(String key, String defaultValue);

    boolean containsValue(String key);

    void putValue(String key, String value);
}
