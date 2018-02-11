package com.oberasoftware.max.web.api.storage;

import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.HomeEntity;
import com.oberasoftware.robo.api.exceptions.DataStoreException;

/**
 * @author renarj
 */
public interface CentralDatastore {
    void beginTransaction();

    void commitTransaction();

    void delete(Class<?> type, String id) throws DataStoreException;

    <T extends HomeEntity> T store(HomeEntity entity) throws DataStoreException;

    Container store(Container container) throws DataStoreException;

    CentralDataDAO getDAO();
}
