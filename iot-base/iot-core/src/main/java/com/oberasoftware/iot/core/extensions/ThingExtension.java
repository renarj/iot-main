package com.oberasoftware.iot.core.extensions;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface ThingExtension extends AutomationExtension {

    List<IotThing> getThings();
}
