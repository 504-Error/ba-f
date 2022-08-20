package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaxiController {

    @GetMapping("/taxi")
    public String setTaxiPage(){
        return "/baf_map/taxi";
    }

    @GetMapping("/taxi/searchAddress")
    public String getDestination(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);
        return "/baf_map/searchAddress";
    }
}

