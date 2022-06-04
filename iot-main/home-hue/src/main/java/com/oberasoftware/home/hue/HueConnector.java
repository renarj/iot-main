package com.oberasoftware.home.hue;

import com.oberasoftware.home.api.model.storage.PluginItem;
import io.github.zeroone3010.yahueapi.Hue;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface HueConnector {
    void connect(Optional<PluginItem> pluginItem);

    List<Hue> getBridges();

    boolean isConnected();
}
