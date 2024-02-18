package com.oberasoftware.home.storage.jasdb;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.model.storage.Container;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.core.index.keys.types.StringKeyType;
import com.oberasoftware.jasdb.core.index.query.SimpleCompositeIndexField;
import com.oberasoftware.jasdb.core.index.query.SimpleIndexField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBCentralDatastore implements CentralDatastore {
    private static final Logger LOG = getLogger(JasDBCentralDatastore.class);

    public static final String THINGS_BAG_NAME = "things";

    @Autowired
    private JasDBSessionFactory jasDBSessionFactory;

    private final Lock lock = new ReentrantLock();

    @Override
    public void beginTransaction() {
        LOG.debug("Locking DB access");
        lock.lock();
    }

    @Override
    public void commitTransaction() {
        LOG.debug("Unlock DB access");
        lock.unlock();
    }

    @PostConstruct
    public void createIndexOnStartup() {
        LOG.debug("Creating a composite index");

        try {
            jasDBSessionFactory.createSession().createOrGetBag(THINGS_BAG_NAME).ensureIndex(
                    new SimpleCompositeIndexField(
                            new SimpleIndexField("controllerId", new StringKeyType()),
                            new SimpleIndexField("pluginId", new StringKeyType()),
                            new SimpleIndexField("thingId", new StringKeyType()),
                            new SimpleIndexField("type", new StringKeyType())
                    ), false);
        } catch (JasDBException e) {
            LOG.error("", e);
        }
    }

    @Override
    public void delete(Class<?> type, String id) throws DataStoreException {
        try {
            DBSession session = jasDBSessionFactory.createSession();
            EntityManager entityManager = session.getEntityManager();
            entityManager.remove(entityManager.findEntity(type, id));
        } catch (JasDBException e) {
            LOG.error("", e);
            throw new DataStoreException("Unable to delete entity: " + id);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IotBaseEntity> T store(IotBaseEntity entity) throws DataStoreException {
        LOG.debug("Storing entity: {}", entity);
        createOrUpdate(entity);

        return (T)entity;
    }

    @Override
    public Container store(Container container) throws DataStoreException {
        LOG.debug("Storing container: {}", container);
        createOrUpdate(container);

        return container;
    }

    private void createOrUpdate(IotBaseEntity entity) throws DataStoreException {
        try {
            DBSession session = jasDBSessionFactory.createSession();
            EntityManager entityManager = session.getEntityManager();
            entityManager.persist(entity);
        } catch (JasDBException e) {
            LOG.error("", e);
            throw new DataStoreException("Unable to store item: " + entity, e);
        } catch(RuntimeException e) {
            LOG.error("", e);
        }
    }
}
