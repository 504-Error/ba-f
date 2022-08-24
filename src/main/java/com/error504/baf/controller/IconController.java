package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class IconController {
    @GetMapping("/icon")
    public String setBusPage() throws IOException {
        return "/baf_map/icon";
    }
}

