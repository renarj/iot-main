package com.oberasoftware.iot.core.extensions;

import com.oberasoftware.iot.core.commands.handlers.CommandHandler;
import com.oberasoftware.iot.core.legacymodel.ExtensionResource;
import com.oberasoftware.iot.core.model.IotThing;

import java.util.Map;
import java.util.Optional;

/**
 * @author renarj
 */
public interface AutomationExtension {
    String getId();

    String getName();

    default boolean supports(ExtensionCapability capability) {
        return false;
    }

    Map<String, String> getProperties();

    CommandHandler getCommandHandler();

    default Optional<ExtensionResource> getIcon() {
        return Optional.empty();
    }

    boolean isReady();

    void activate(IotThing pluginThing);

    void activate();
}
