package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DirectionController {
    @GetMapping("/direction")
    public String test() {
        return "/baf_map/direction";
    }
}
