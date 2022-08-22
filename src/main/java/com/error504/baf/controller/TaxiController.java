package com.error504.baf.controller;

import com.error504.baf.model.SiteUser;
import com.error504.baf.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class TaxiController {
    private UserService userService;

    @GetMapping("/taxi")
    public String setTaxiPage(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
//        model.addAttribute("name", name);

        return "/baf_map/taxi";
    }

    @GetMapping("/taxi/searchAddress")
    public String getDestination(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);
        return "/baf_map/searchAddress";
    }
}

