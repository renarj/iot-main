package com.oberasoftware.iot.core.extensions;

import com.oberasoftware.iot.core.exceptions.IOTException;

import java.util.List;
import java.util.Optional;

/**
 * @author renarj
 */
public interface ExtensionManager {
    void activateController(String controllerId) throws IOTException;

    void activateExtensions() throws IOTException;

    List<AutomationExtension> getExtensions();

    Optional<AutomationExtension> getExtension(String extensionId);
}
