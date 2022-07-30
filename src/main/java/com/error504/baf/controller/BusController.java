package com.error504.baf.controller;

import com.error504.baf.service.BusService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class BusController {
    private BusService busService;

    @GetMapping("/bus")
    public String callBusApi() throws IOException {
        busService.StationByCoordinate();
        return "/baf_map/bus";
    }

}
