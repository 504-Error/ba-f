package com.error504.baf.controller;

import com.error504.baf.model.SiteUser;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class TaxiController {
    private final UserService userService;

    @Autowired
    public TaxiController(UserService userService){
        this.userService = userService;
    }

//    @PreAuthorize("isAuthenticated()")

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taxi")
    public String setTaxiPage(Model model, Principal principal){
        if (principal != null) {
            SiteUser siteUser = userService.getUser(principal.getName());
            model.addAttribute("siteUser", siteUser);
        }

        return "/baf_map/taxi";
    }

    @GetMapping("/taxi/searchAddress")
    public String getDestination(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);

        return "/baf_map/searchAddress";
    }
}

