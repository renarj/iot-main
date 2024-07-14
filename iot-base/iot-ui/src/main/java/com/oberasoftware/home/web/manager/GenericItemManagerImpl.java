package com.oberasoftware.home.web.manager;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.managers.GenericItemManager;
import com.oberasoftware.iot.core.model.storage.VirtualItem;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
public abstract class GenericItemManagerImpl<T extends VirtualItem> implements GenericItemManager<T> {
    private static final Logger LOG = getLogger(GenericItemManagerImpl.class);

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private HomeDAO homeDAO;

    @Override
    public List<? extends T> getItems() {
        return homeDAO.findVirtualItems(getType());
    }

    @Override
    public List<? extends T> getItems(String controllerId) {
        return homeDAO.findVirtualItems(getType(), controllerId);
    }

    @Override
    public T getItem(String groupId) {
        return homeDAO.findItem(getType(), groupId).get();
    }

    @Override
    public T store(T groupItem) {
        try {
            return centralDatastore.store(groupItem);
        } catch (DataStoreException e) {
            LOG.error("", e);
        }
        return null;
    }

    @Override
    public void delete(String groupId) {
        centralDatastore.beginTransaction();
        try {
            centralDatastore.delete(getType(), groupId);
        } catch (DataStoreException e) {
            LOG.error("", e);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    protected abstract Class<? extends T> getType();

}
