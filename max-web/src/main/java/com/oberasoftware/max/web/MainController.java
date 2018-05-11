package com.oberasoftware.max.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author renarj
 */
@Controller
public class MainController {
    @RequestMapping("/dash")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/joystick")
    public String getS() {
        return "simple";
    }

    @RequestMapping("/walk")
    public String getWalker() {
        return "walker";
    }

}
