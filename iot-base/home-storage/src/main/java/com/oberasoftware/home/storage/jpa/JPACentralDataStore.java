package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.model.storage.Container;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class JPACentralDataStore implements CentralDatastore {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void beginTransaction() {

    }

    @Override
    public void commitTransaction() {

    }

    @Override
    public void delete(Class<?> type, String id) throws DataStoreException {

    }

    @Override
    public <T extends IotBaseEntity> T store(IotBaseEntity entity) throws DataStoreException {
        entityManager.getTransaction();
        return null;
    }

    @Override
    public Container store(Container container) throws DataStoreException {
        return null;
    }
}
