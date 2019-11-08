package com.oberasoftware.robo.maximus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JointEditorController {
    @RequestMapping("/joints")
    public String getIndex() {
        return "joints";
    }

}
