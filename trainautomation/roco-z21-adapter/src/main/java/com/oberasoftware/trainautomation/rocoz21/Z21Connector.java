package com.oberasoftware.trainautomation.rocoz21;

import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.trainautomation.rocoz21.commands.Z21Command;

public interface Z21Connector {
    void connect() throws IOTException;

    void disconnect() throws IOTException;

    void send(Z21Command command) throws IOTException;
}
