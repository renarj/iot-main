package com.oberasoftware.max.web.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.Dashboard;
import com.oberasoftware.max.web.api.model.HomeEntity;
import com.oberasoftware.max.web.api.model.Widget;
import com.oberasoftware.max.web.api.storage.CentralDataDAO;
import com.oberasoftware.max.web.storage.model.ContainerImpl;
import com.oberasoftware.max.web.storage.model.DashboardImpl;
import com.oberasoftware.max.web.storage.model.WidgetImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBDAO implements CentralDataDAO {
    private static final Logger LOG = getLogger(JasDBDAO.class);

    @Autowired
    private JasDBSessionFactory sessionFactory;

    @Override
    public <T extends HomeEntity> Optional<T> findItem(Class<T> type, String id) {
        try {
            DBSession session = sessionFactory.createSession();
            EntityManager entityManager = session.getEntityManager();

            T result = entityManager.findEntity(type, id);
            return Optional.ofNullable(result);
        } catch(JasDBException e) {
            LOG.error("Unable to load item", e);
            return empty();
        }
    }

    @Override
    public Optional<Container> findContainer(String id) {
        try {
            DBSession session = sessionFactory.createSession();
            EntityManager entityManager = session.getEntityManager();

            ContainerImpl container = entityManager.findEntity(ContainerImpl.class, id);
            return Optional.of(container);
        } catch(JasDBException e) {
            LOG.error("Unable to load container", e);
            return empty();
        }
    }

    @Override
    public List<Container> findDashboardContainers(String dashboardId) {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("dashboardId", dashboardId).build()));
    }

    @Override
    public List<Container> findContainers() {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .build()));
    }

    @Override
    public List<Container> findContainers(String parentId) {
        return newArrayList(findItems(ContainerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("parentContainerId", parentId).build()));
    }

    @Override
    public List<Dashboard> findDashboards() {
        return newArrayList(findItems(DashboardImpl.class, new ImmutableMap.Builder<String, String>()
                .build(), newArrayList("weight")));
    }

    @Override
    public List<Widget> findWidgets(String containerId) {
        return newArrayList(findItems(WidgetImpl.class, new ImmutableMap.Builder<String, String>()
                .put("containerId", containerId).build()));
    }

    private <T> T findItem(Class<T> type, Map<String, String> properties) {
        List<T> items = findItems(type, properties);
        return Iterables.getFirst(items, null);
    }

    private <T> List<T> findItems(Class<T> type, Map<String, String> properties) {
        return findItems(type, properties, new ArrayList<>());
    }

    private <T> List<T> findItems(Class<T> type, Map<String, String> properties, List<String> orderedBy) {
        List<T> results = new ArrayList<>();

        try {
            DBSession session = sessionFactory.createSession();
            EntityManager entityManager = session.getEntityManager();

            QueryBuilder queryBuilder = QueryBuilder.createBuilder();
            properties.forEach((k, v) -> queryBuilder.field(k).value(v));
            orderedBy.forEach(queryBuilder::sortBy);

            return entityManager.findEntities(type, queryBuilder);
        } catch (JasDBException e) {
            LOG.error("Unable to query JasDB", e);
        }
        return results;
    }
}
