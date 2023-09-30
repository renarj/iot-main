package com.oberasoftware.home.data;

import com.oberasoftware.iot.core.managers.TemplateManager;
import com.oberasoftware.iot.core.model.ThingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/templates")
public class TemplateRestSvc {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateRestSvc.class);

    @Autowired
    private TemplateManager templateManager;

    @RequestMapping(value = "({pluginId})", method = RequestMethod.GET)
    public List<ThingTemplate> getTemplates(@PathVariable String pluginId) {
        LOG.debug("Requested All Templates for plugin: {}", pluginId);

        return templateManager.findTemplates(pluginId);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Set<String> getPluginsAvailable() {
        LOG.debug("Requested all available plugin names");
        return templateManager.findPlugins();
    }
}
