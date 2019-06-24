package com.oberasoftware.robo.maximus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MotionEditorController {
    @RequestMapping("/motion")
    public String getIndex() {
        return "motion";
    }
}
