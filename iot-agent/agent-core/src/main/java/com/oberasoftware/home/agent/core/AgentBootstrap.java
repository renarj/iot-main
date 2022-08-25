package com.oberasoftware.home.agent.core;

import com.oberasoftware.home.agent.core.ui.AgentConfig;
import com.oberasoftware.iot.core.exceptions.IOTException;

public interface AgentBootstrap {
    boolean startAgent() throws IOTException;

    boolean reload() throws IOTException;

    boolean configure(AgentConfig agentConfig) throws IOTException;
}
