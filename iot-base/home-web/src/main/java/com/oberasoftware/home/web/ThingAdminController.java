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
@RequestMapping("/web/admin/things")
public class ThingAdminController {
    private static final Logger LOG = getLogger(ThingAdminController.class);

    @RequestMapping
    public String showIndex() {
        LOG.debug("Showing admin screen - controllers");

        return "admin/things";
    }

    @RequestMapping("/controllers({controllerId})")
    public String showController(@PathVariable String controllerId, Model model) {
        model.addAttribute("controllerId", controllerId);
        return "admin/things";
    }

    @RequestMapping("/controllers({controllerId})/things({thingId})")
    public String showThing(@PathVariable String controllerId, @PathVariable String thingId, Model model) {
        model.addAttribute("controllerId", controllerId);
        model.addAttribute("thingId", thingId);
        return "admin/things";
    }

    @RequestMapping(value = "/icon/plugin/{pluginId}")
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
