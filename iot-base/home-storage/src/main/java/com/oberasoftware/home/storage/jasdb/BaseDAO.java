package com.oberasoftware.home.storage.jasdb;

import com.google.common.collect.Iterables;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class BaseDAO {
    private static final Logger LOG = getLogger(BaseDAO.class);

    @Autowired
    private JasDBSessionFactory sessionFactory;

    protected EntityManager getEntityManager() throws JasDBException {
        DBSession session = sessionFactory.createSession();
        return session.getEntityManager();
    }

    protected <T> T findItem(Class<T> type, Map<String, String> properties) {
        List<T> items = findItems(type, properties);
        return Iterables.getFirst(items, null);
    }

    protected <T> List<T> findItems(Class<T> type, Map<String, String> properties) {
        return findItems(type, properties, new ArrayList<>());
    }

    protected <T> List<T> findItems(Class<T> type, Map<String, String> properties, List<String> orderedBy) {
        List<T> results = new ArrayList<>();

        try {
            QueryBuilder queryBuilder = QueryBuilder.createBuilder();
            properties.forEach((k, v) -> queryBuilder.field(k).value(v));
            orderedBy.forEach(queryBuilder::sortBy);

            return getEntityManager().findEntities(type, queryBuilder);
        } catch (JasDBException e) {
            LOG.error("Unable to query JasDB", e);
        }
        return results;
    }
}
