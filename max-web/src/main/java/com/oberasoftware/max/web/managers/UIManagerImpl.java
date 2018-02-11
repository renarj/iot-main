package com.oberasoftware.max.web.managers;

import com.oberasoftware.home.api.exceptions.DataStoreException;
import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import com.oberasoftware.max.web.api.managers.UIManager;
import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.MutableItem;
import com.oberasoftware.max.web.api.model.Widget;
import com.oberasoftware.max.web.api.storage.CentralDataDAO;
import com.oberasoftware.max.web.api.storage.CentralDatastore;
import com.oberasoftware.max.web.storage.model.ContainerImpl;
import com.oberasoftware.max.web.storage.model.WidgetImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class UIManagerImpl implements UIManager {
    private static final Logger LOG = getLogger(UIManagerImpl.class);

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private CentralDataDAO homeDAO;

    @Override
    public List<Container> getDashboardContainers(String dashboardId) {
        return homeDAO.findDashboardContainers(dashboardId);
    }

    @Override
    public List<Container> getAllContainers() {
        return homeDAO.findContainers();
    }

    @Override
    public List<Container> getChildren(String containerId) {
        return homeDAO.findContainers(containerId);
    }

    @Override
    public Container getContainer(String containerId) {
        try {
            return homeDAO.findContainer(containerId).get();
        } catch(Exception e) {
            LOG.error("", e);
            throw new RuntimeHomeAutomationException("Error", e);
        }
    }

    @Override
    public List<Widget> getItems(String containerId) {
        return homeDAO.findWidgets(containerId);
    }

    @Override
    public void setWidgetProperty(String itemId, String property, String value) {
        setItemProperty(WidgetImpl.class, itemId, property, value);
    }

    @Override
    public void setContainerProperty(String itemId, String property, String value) {
        setItemProperty(ContainerImpl.class, itemId, property, value);
    }

    private <T extends MutableItem> void setItemProperty(Class<T> type, String itemId, String property, String value) {
        centralDatastore.beginTransaction();
        try {
            Optional<T> optionalItem = homeDAO.findItem(type, itemId);
            if(optionalItem.isPresent()) {
                T item = optionalItem.get();
                Map<String, String> properties = new HashMap<>(item.getProperties());
                properties.put(property, value);

                item.setProperties(properties);
                store(item);
            }

        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public void setParentContainer(String itemId, String parentContainerId) {
        centralDatastore.beginTransaction();
        try {
            Optional<WidgetImpl> item = homeDAO.findItem(WidgetImpl.class, itemId);
            if (item.isPresent()) {
                WidgetImpl uiItem = item.get();
                uiItem.setContainerId(parentContainerId);
                store(uiItem);
            }
        } finally {
            centralDatastore.commitTransaction();
        }
    }


    @Override
    public void deleteContainer(String containerId) {
        centralDatastore.beginTransaction();
        try {
            List<Container> children = getChildren(containerId);
            children.forEach(c -> deleteContainer(c.getId()));

            getItems(containerId).forEach(i -> delete(WidgetImpl.class, i.getId()));
            delete(ContainerImpl.class, containerId);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public void deleteWidget(String itemId) {
        delete(WidgetImpl.class, itemId);
    }

    private void delete(Class<?> type, String itemId) {
        centralDatastore.beginTransaction();
        try {

            centralDatastore.delete(type, itemId);
        } catch (DataStoreException e) {
            LOG.error("", e);
        } finally {
            centralDatastore.commitTransaction();
        }
    }

    @Override
    public <T extends MutableItem> T store(T item) {
        centralDatastore.beginTransaction();
        try {
            return centralDatastore.store(item);
        } catch (DataStoreException e) {
            LOG.error("Unable to store item", e);
        } catch(Exception ex) {
            LOG.error("", ex);
        } finally {
            centralDatastore.commitTransaction();
        }
        return null;
    }
}
