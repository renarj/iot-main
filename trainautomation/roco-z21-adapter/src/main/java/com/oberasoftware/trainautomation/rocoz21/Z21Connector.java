package com.oberasoftware.trainautomation.rocoz21;

import com.oberasoftware.trainautomation.rocoz21.commands.Z21Command;

public interface Z21Connector {
    void connect() throws HomeAutomationException;

    void disconnect() throws HomeAutomationException;

    void send(Z21Command command) throws HomeAutomationException;
}
