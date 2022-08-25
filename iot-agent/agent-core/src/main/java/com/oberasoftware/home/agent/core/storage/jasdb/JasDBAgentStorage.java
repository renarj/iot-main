package com.oberasoftware.home.agent.core.storage.jasdb;

import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.Entity;
import com.oberasoftware.jasdb.api.session.EntityBag;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import com.oberasoftware.jasdb.api.session.query.QueryResult;
import com.oberasoftware.jasdb.core.SimpleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class JasDBAgentStorage implements AgentStorage {
    private static final Logger LOG = LoggerFactory.getLogger(JasDBAgentStorage.class);

    private static final String DATA_BAG = "agent";

    @Autowired
    private JasDBSessionFactory jasDBSessionFactory;

    @Override
    public String getValue(String key) {
        return getValue(key, null);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        Entity entity = createOrGetAgentConfig();
        if(entity.hasProperty(key)) {
            return entity.getValue(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean containsValue(String key) {
        return createOrGetAgentConfig().hasProperty(key);
    }

    @Override
    public void putValue(String key, String value) {
        Entity entity = createOrGetAgentConfig();
        entity.setProperty(key, value);

        try {
            persist(entity);
        } catch (JasDBException e) {
            throw new RuntimeIOTException("Unable to store agent configuration value", e);
        }
    }

    private Entity createOrGetAgentConfig() {
        try {
            EntityBag bag = getBag();
            QueryResult result = bag.find(QueryBuilder.createBuilder().field("configurationItem").value("agent")).execute();
            if(result.hasNext()) {
                Entity entity = result.next();
                LOG.debug("Found existing agent configuration: {}", entity);
                return entity;
            } else {
                SimpleEntity entity = new SimpleEntity();
                entity.addProperty("configurationItem", "agent");
                LOG.debug("Creating new agent configuration: {}", entity);
                return persist(bag, entity);
            }
        } catch(JasDBException e) {
            throw new RuntimeIOTException("Unable to get agent configuration", e);
        }

    }

    private Entity persist(EntityBag bag, Entity entity) throws JasDBException {
        return bag.persist(entity);
    }

    private Entity persist(Entity entity) throws JasDBException {
        return persist(getBag(), entity);
    }

    private EntityBag getBag() throws JasDBException {
        DBSession session = jasDBSessionFactory.createSession();
        return session.createOrGetBag(DATA_BAG);
    }
}
