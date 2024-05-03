package com.oberasoftware.home.web.manager;

import com.oberasoftware.home.web.manager.GenericItemManagerImpl;
import com.oberasoftware.iot.core.managers.VirtualItemManager;
import com.oberasoftware.iot.core.model.storage.VirtualItem;
import com.oberasoftware.iot.core.model.storage.impl.VirtualItemImpl;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class VirtualItemManagerImpl extends GenericItemManagerImpl<VirtualItem> implements VirtualItemManager {
    @Override
    protected Class<? extends VirtualItem> getType() {
        return VirtualItemImpl.class;
    }
}
