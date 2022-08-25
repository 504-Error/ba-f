package com.error504.baf.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.io.IOException;

@Controller
public class TaxiController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/taxi")
    public String setTaxiPage() throws IOException {
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
