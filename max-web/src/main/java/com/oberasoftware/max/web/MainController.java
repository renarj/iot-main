package com.oberasoftware.max.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author renarj
 */
@Controller
public class MainController {
    @RequestMapping("/")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/s")
    public String getS() {
        return "simple";
    }

}
