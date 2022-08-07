package com.oberasoftware.home.hue;

import java.util.List;

/**
 * @author renarj
 */
public interface HueConnector {
    void connect();

    List<HueBridge> getBridges();

    boolean isConnected();
}
