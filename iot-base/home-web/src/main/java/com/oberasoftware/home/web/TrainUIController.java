package com.oberasoftware.home.web;

import com.oberasoftware.iot.core.exceptions.IOTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/trains")
public class TrainUIController {
    private static final Logger LOG = LoggerFactory.getLogger(TrainUIController.class);

    @RequestMapping
    public String getTrains(Model model) throws IOTException {
        LOG.debug("Showing Train Admin screen - controllers");

        return "train/index";
    }

    @RequestMapping("/sensors")
    public String getSensors() {
        return "train/sensors";
    }

    @RequestMapping("/system")
    public String getAdmin() {
        return "train/system";
    }

}
