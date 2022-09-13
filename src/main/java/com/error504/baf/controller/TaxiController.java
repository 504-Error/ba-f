package com.error504.baf.controller;

import com.error504.baf.model.SiteUser;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.io.IOException;
import java.security.Principal;

@Controller
public class TaxiController {
    private final UserService userService;

    @Autowired
    public TaxiController(UserService userService){
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taxi")
    public String setTaxiPage(Model model, Principal principal){
        if (principal != null) {
            SiteUser siteUser = userService.getUser(principal.getName());
            model.addAttribute("siteUser", siteUser);
        }

        model.addAttribute("tab", "map");

        return "/baf_map/taxi";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taxi/searchDes")
    public String getDestination() {

        return "/baf_map/searchDes";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taxi/searchSource")
    public String getSource() {

        return "/baf_map/searchSource";
    }

}
