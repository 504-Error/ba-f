package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IconController {
    @GetMapping("/icon")
    public String setBusPage() {
        return "/baf_map/icon";
    }
}

