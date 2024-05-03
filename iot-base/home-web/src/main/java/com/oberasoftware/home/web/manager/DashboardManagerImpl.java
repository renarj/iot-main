package com.oberasoftware.home.web.manager;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import com.oberasoftware.iot.core.managers.DashboardManager;
import com.oberasoftware.iot.core.managers.UIManager;
import com.oberasoftware.iot.core.model.storage.Dashboard;
import com.oberasoftware.iot.core.model.storage.impl.DashboardImpl;
import com.oberasoftware.iot.core.storage.CentralDatastore;
import com.oberasoftware.iot.core.storage.HomeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * @author Renze de Vries
 */
@Component
public class DashboardManagerImpl implements DashboardManager {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardManagerImpl.class);

    @Autowired
    private HomeDAO homeDAO;

    @Autowired
    private CentralDatastore centralDatastore;

    @Autowired
    private UIManager uiManager;

    @PostConstruct
    public void initializeDefault() {
        LOG.info("Ensuring default dashboard exists");

        if(homeDAO.findDashboards().isEmpty()) {
            LOG.debug("No dashboards present, ensuring a default dashboard");
            store(new DashboardImpl(null, "Default", 0));
        }
    }

    @Override
    public List<Dashboard> findDashboards() {
        return homeDAO.findDashboards();
    }

    @Override
    public Dashboard findDefaultDashboard() {
        return findDashboards().stream().findFirst().orElseThrow(()
                -> new RuntimeIOTException("Unable to determine default dashboard"));
    }

    @Override
    public Dashboard findDashboard(String dashboardId) {
        Optional<DashboardImpl> d =  homeDAO.findItem(DashboardImpl.class, dashboardId);
        return d.get();
    }

    @Override
    public Dashboard store(Dashboard dashboard) {
        centralDatastore.beginTransaction();
        try {
            return centralDatastore.store(dashboard);
        } catch (DataStoreException e) {
            LOG.error("", e);
        } finally {
            centralDatastore.commitTransaction();
        }
        return null;
    }

    @Override
    public void delete(String dashboardId) {
        centralDatastore.beginTransaction();
        try {
            LOG.debug("Deleting containers for dashboard: {}", dashboardId);
            uiManager.getDashboardContainers(dashboardId).forEach(c -> uiManager.deleteContainer(c.getId()));

            LOG.debug("Deleting dashboard: {}", dashboardId);
            centralDatastore.delete(DashboardImpl.class, dashboardId);
        } catch (DataStoreException e) {
            LOG.error("", e);
        } finally {
            centralDatastore.commitTransaction();
        }
    }
}
