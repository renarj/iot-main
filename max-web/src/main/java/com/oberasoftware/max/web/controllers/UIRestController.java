package com.oberasoftware.max.web.controllers;

import com.oberasoftware.max.web.api.managers.UIManager;
import com.oberasoftware.max.web.api.model.Container;
import com.oberasoftware.max.web.api.model.Widget;
import com.oberasoftware.max.web.storage.model.ContainerImpl;
import com.oberasoftware.max.web.storage.model.WidgetImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@RestController
@RequestMapping("/ui")
public class UIRestController {
    private static final Logger LOG = getLogger(UIRestController.class);

    @Autowired
    private UIManager uiManager;

    @RequestMapping("/containers({containerId})")
    public Container getContainer(@PathVariable String containerId) {
        return uiManager.getContainer(containerId);
    }

    @RequestMapping("/containers")
    public List<Container> getContainers() {
        return uiManager.getAllContainers();
    }

    @RequestMapping("/containers({containerId})/children")
    public List<Container> getContainers(@PathVariable String containerId) {
        return uiManager.getChildren(containerId);
    }

    @RequestMapping(value = "/containers({containerId})", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void deleteContainer(@PathVariable String containerId) {
        uiManager.deleteContainer(containerId);
    }

    @RequestMapping("/dashboard({dashboardId})/containers")
    public List<Container> getDashboardContainers(@PathVariable String dashboardId) {
        return uiManager.getDashboardContainers(dashboardId);
    }

    @RequestMapping("/containers({containerId})/widgets")
    public List<Widget> getWidgets(@PathVariable String containerId) {
        return uiManager.getItems(containerId);
    }

    @RequestMapping(value = "/widgets", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Widget createWidget(@RequestBody WidgetImpl widget) {
        return uiManager.store(widget);
    }

    @RequestMapping(value = "/widgets({widgetId})", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWidget(@PathVariable String widgetId) {
        uiManager.deleteWidget(widgetId);
    }

    @RequestMapping(value = "/widgets/({widgetId})/setParent({containerId})", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public void setParentContainer(@PathVariable String widgetId, @PathVariable String containerId) {
        LOG.debug("Setting widget: {} parent container: {}", widgetId, containerId);
        uiManager.setParentContainer(widgetId, containerId);
    }

    @RequestMapping(value = "/widgets/({widgetId})/setProperty({property},{value})", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void setProperty(@PathVariable String widgetId, @PathVariable String property, @PathVariable String value) {
        LOG.debug("Setting widget: {} property: {} to value: {}", widgetId, property, value);
        uiManager.setWidgetProperty(widgetId, property, value);
    }

    @RequestMapping(value = "/containers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Container createContainer(@RequestBody ContainerImpl container) {
        LOG.debug("Container creation: {}", container);
        return uiManager.store(container);
    }
}
