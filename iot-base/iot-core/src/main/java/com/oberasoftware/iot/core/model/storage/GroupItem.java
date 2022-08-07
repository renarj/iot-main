package com.oberasoftware.iot.core.model.storage;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GroupItem extends VirtualItem {
    List<String> getDeviceIds();
}
