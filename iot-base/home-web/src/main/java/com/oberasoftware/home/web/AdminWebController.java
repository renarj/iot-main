package com.oberasoftware.home.web;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Controller
@RequestMapping("/web/admin")
public class AdminWebController {
    private static final Logger LOG = getLogger(AdminWebController.class);

    @RequestMapping("/groups/")
    public String getGroups() {
        return "admin/groups";
    }

    @RequestMapping("/things")
    public String showThingIndex() {
        LOG.debug("Showing admin screen - things");

        return "admin/things";
    }

    @RequestMapping("/sysconfig")
    public String showSysConfig() {
        LOG.debug("Showing Config admin screen");

        return "admin/sysconfig";
    }

    @RequestMapping("/sysconfig/plugins({pluginId})")
    public String showSysConfig(@PathVariable String pluginId, Model model) {
        model.addAttribute("pluginId", pluginId);
        return "admin/sysconfig";
    }

    @RequestMapping("/sysconfig/plugins({pluginId})/schemas({schemaId})")
    public String showSysConfig(@PathVariable String pluginId, @PathVariable String schemaId, Model model) {
        model.addAttribute("pluginId", pluginId);
        model.addAttribute("schemaId", schemaId);
        return "admin/sysconfig";
    }

    @RequestMapping("/things/controllers({controllerId})")
    public String showController(@PathVariable String controllerId, Model model) {
        model.addAttribute("controllerId", controllerId);
        return "admin/things";
    }

    @RequestMapping("/things/controllers({controllerId})/things({thingId})")
    public String showThing(@PathVariable String controllerId, @PathVariable String thingId, Model model) {
        model.addAttribute("controllerId", controllerId);
        model.addAttribute("thingId", thingId);
        return "admin/things";
    }

    @RequestMapping(value = "/things/icon/plugin/{pluginId}")
    public @ResponseBody ResponseEntity<byte[]> getIcon(@PathVariable String pluginId) throws IOException {
        LOG.debug("Requesting icon for plugin: {}", pluginId);

//        AutomationExtension extension = extensionManager.getExtensionById(pluginId).get();
//        Optional<ExtensionResource> optionalResource = extension.getIcon();
//        if(optionalResource.isPresent()) {
//            ExtensionResource resource = optionalResource.get();
//            byte[] data = ByteStreams.toByteArray(resource.getStream());
//
//            HttpHeaders header = new HttpHeaders();
//            header.setContentType(MediaType.parseMediaType(resource.getMediaType()));
//
//            return new ResponseEntity<>(data, header, HttpStatus.OK);
//        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
    }
}
