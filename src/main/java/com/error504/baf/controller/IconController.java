package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IconController {
    @GetMapping("/icon")
    public String setBusPage(Model model) {
        model.addAttribute("tab", "map");

        return "/baf_map/icon";
    }
}

