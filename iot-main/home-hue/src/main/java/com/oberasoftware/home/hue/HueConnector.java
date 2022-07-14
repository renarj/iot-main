package com.oberasoftware.home.hue;

import com.oberasoftware.iot.core.model.storage.PluginItem;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface HueConnector {
    void connect(Optional<PluginItem> pluginItem);

    List<HueBridge> getBridges();

    boolean isConnected();
}
