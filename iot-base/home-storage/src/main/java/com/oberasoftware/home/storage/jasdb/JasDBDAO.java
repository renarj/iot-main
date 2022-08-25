package com.oberasoftware.home.storage.jasdb;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.iot.core.model.IotBaseEntity;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.*;
import com.oberasoftware.iot.core.model.storage.impl.*;
import com.oberasoftware.iot.core.storage.HomeDAO;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBDAO implements HomeDAO {
    private static final Logger LOG = getLogger(JasDBDAO.class);

    @Autowired
    private JasDBSessionFactory sessionFactory;

    @Override
    public <T extends IotBaseEntity> Optional<T> findItem(Class<T> type, String id) {
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

    @Override
    public Optional<Controller> findController(String controllerId) {
        ControllerImpl controllerItem = findItem(ControllerImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId).build());
        return ofNullable(controllerItem);
    }

    @Override
    public List<Controller> findControllers() {
        return newArrayList(findItems(ControllerImpl.class, new ImmutableMap.Builder<String, String>().build()));
    }

    @Override
    public Optional<IotThing> findThing(String controllerId, String deviceId) {
        IotThingImpl deviceItem = findItem(IotThingImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .put("thingId", deviceId).build());
        return ofNullable(deviceItem);
    }

    @Override
    public List<IotThing> findThings(String controllerId) {
        return newArrayList(findItems(IotThingImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type) {
        return newArrayList(findItems(type, new HashMap<>()));
    }

    @Override
    public <T extends VirtualItem> List<T> findVirtualItems(Class<T> type, String controllerId) {
        return newArrayList(findItems(type, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public List<RuleItem> findRules(String controllerId) {
        return newArrayList(findItems(RuleItemImpl.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public List<RuleItem> findRules() {
        return newArrayList(findItems(RuleItemImpl.class, new HashMap<>()));
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
