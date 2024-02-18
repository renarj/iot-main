package com.oberasoftware.iot.integrations.shelly;

import com.oberasoftware.iot.core.commands.SwitchCommand;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.states.Value;

import java.util.List;
import java.util.Map;

public interface ShellyConnector {
    ShellyMetadata getShellyInfo(String controllerId, String thingId, String shellyIp) throws IOTException;

    Map<String, Value> getValues(String shellyIp, List<String> components) throws IOTException;

    boolean setRelay(String shellyIp, int relay, SwitchCommand.STATE state) throws IOTException;
}
