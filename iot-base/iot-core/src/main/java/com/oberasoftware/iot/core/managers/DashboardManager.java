package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.model.storage.Dashboard;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface DashboardManager {
    List<Dashboard> findDashboards();

    Dashboard findDefaultDashboard();

    Dashboard findDashboard(String dashboardId);

    Dashboard store(Dashboard dashboard);

    void delete(String dashboardId);
}
