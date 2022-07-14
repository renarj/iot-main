package com.oberasoftware.home.api.storage;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.model.storage.Container;

/**
 * @author renarj
 */
public interface CentralDatastore {
    void beginTransaction();

    void commitTransaction();

    void delete(Class<?> type, String id) throws DataStoreException;

    <T extends IotBaseEntity> T store(IotBaseEntity entity) throws DataStoreException;

    Container store(Container container) throws DataStoreException;

    HomeDAO getDAO();
}
