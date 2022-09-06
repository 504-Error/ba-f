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
    private final BusService busService;

    @Autowired
    public BusController(BusService busService){
        this.busService = busService;
    }

    @GetMapping("/bus")
    public String setBusPage() {
        return "/baf_map/bus";
    }

    @PostMapping("/bus/search-station")
    @ResponseBody
    public ArrayList searchStation(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList result = busService.getStationsByPos(input.get("LAT").toString(), input.get("LNG").toString());

        ArrayList<String> lowBusNumList =  new ArrayList<>();

        for (Object stationData : result) {

            String[] arr = stationData.toString().split(",");
            String[] arr2 = arr[7].split("=");


            ArrayList stationInfo = busService.getLowStationByUid(arr2[1]);
            // if(stationInfo != null ) { System.out.println(stationInfo.toString()); }
            String lowBusExisting = "true";
            if(stationInfo == null){
                lowBusExisting = "false";
            }

            lowBusNumList.add(lowBusExisting);
        }

        ArrayList<ArrayList<String>> resultArray = new ArrayList<>();
        resultArray.add(result);
        resultArray.add(lowBusNumList);

        return resultArray;
    }

    @PostMapping("/bus/station-info")
    @ResponseBody
    public ArrayList stationInfo(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList stationInfo = busService.getLowStationByUid(input.get("arsId").toString());

        return stationInfo;
    }

    @PostMapping("/bus/lowBus-num")
    @ResponseBody
    public String lowBusExisting(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList stationInfo = busService.getLowStationByUid(input.get("arsId").toString());
        String lowBusExisting = "true";
        if(stationInfo == null){
            lowBusExisting = "false";
        }
        return lowBusExisting;
    }

    @PostMapping("/bus/bus-info")
    @ResponseBody
    public ArrayList[] busInfo(@RequestBody Map<String, Object> input) throws IOException {
        ArrayList[] result = new ArrayList[2];
        result[0] = busService.getRouteInfoItem(input.get("busRouteId").toString());
        result[1] = busService.getStaionsByRouteList(input.get("busRouteId").toString());

        return result;
    }
}
