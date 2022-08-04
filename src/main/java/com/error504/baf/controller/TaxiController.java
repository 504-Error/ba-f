package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaxiController {
    @GetMapping("/taxi")
    public String test() {
        return "/baf_map/taxi";
    }
}
