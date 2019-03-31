package com.oberasoftware.robo.maximus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JointEditor {
    @RequestMapping("/joints")
    public String getIndex() {
        return "joints";
    }

}
