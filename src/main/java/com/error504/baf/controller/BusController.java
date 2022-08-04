package com.error504.baf.controller;

import com.error504.baf.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


@Controller
public class BusController {
    private BusService busService;

    @Autowired
    public BusController(BusService busService){
        this.busService = busService;
    }

    @GetMapping("/bus")
    public String callBusApi() throws IOException {
        return "/baf_map/bus";
    }

    @PostMapping("/bus/search-station")
    @ResponseBody
    public ArrayList searchStation(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList result = busService.getStationsByPos(input.get("LAT").toString(), input.get("LNG").toString());

        return result;
    }

    @PostMapping("/bus/station-info")
    @ResponseBody
    public ArrayList stationInfo(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList stationInfo = busService.getLowStaionByUid(input.get("arsId").toString());

        return stationInfo;
    }
}
