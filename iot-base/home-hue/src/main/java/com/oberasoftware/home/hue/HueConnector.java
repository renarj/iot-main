package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;

/**
 * @author renarj
 */
public interface HueConnector {
    void connect(IotThing pluginData);

    List<HueBridge> getBridges();

    boolean isConnected();
}
