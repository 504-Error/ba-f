package com.error504.baf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class BusService {

    // 좌표 기반으로 주변 버스 정류장 위치 정보 구하는 함수
    public ArrayList getStationsByPos(String LAT, String LNG) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "j1X7vFE5RUb9KZAjevsWKZlhE5D3j53C4nqmug7%2Fe8se2gMQgV4Nm6jYuAdHW29ey3Ucn%2FsBwkIScNFRG5jt1g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(LNG, "UTF-8")); /*기준위치 X*/
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(LAT, "UTF-8")); /*기준위치 Y*/
        urlBuilder.append("&" + URLEncoder.encode("radius","UTF-8") + "=" + URLEncoder.encode("450", "UTF-8")); /*검색 반경*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답 유형*/
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        HashMap<String, HashMap<String, ArrayList>> result = new ObjectMapper().readValue(rd, HashMap.class);

        rd.close();
        conn.disconnect();

        HashMap<String, ArrayList> res = result.get("msgBody");
//        System.out.println(res.get("itemList"));

        ArrayList resArr = res.get("itemList");

        return resArr;
    }

    // 고유 아이디로 버스 정류장 정보 구하기(남은 시간 등)
    public ArrayList getLowStaionByUid(String arsId) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "j1X7vFE5RUb9KZAjevsWKZlhE5D3j53C4nqmug7%2Fe8se2gMQgV4Nm6jYuAdHW29ey3Ucn%2FsBwkIScNFRG5jt1g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("arsId","UTF-8") + "=" + URLEncoder.encode(arsId, "UTF-8")); /*정류장 고유 ID*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답 유형*/
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        HashMap<String, HashMap<String, ArrayList>> result = new ObjectMapper().readValue(rd, HashMap.class);

        rd.close();
        conn.disconnect();

        HashMap<String, ArrayList> res = result.get("msgBody");
        System.out.println(res.get("itemList"));

        ArrayList resArr = res.get("itemList");

        return resArr;
    }

    // 버스 기본 정보
    public ArrayList getRouteInfoItem(String busRouteId) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/busRouteInfo/getRouteInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "j1X7vFE5RUb9KZAjevsWKZlhE5D3j53C4nqmug7%2Fe8se2gMQgV4Nm6jYuAdHW29ey3Ucn%2FsBwkIScNFRG5jt1g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("busRouteId","UTF-8") + "=" + URLEncoder.encode(busRouteId, "UTF-8")); /*정류장 고유 ID*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답 유형*/
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        HashMap<String, HashMap<String, ArrayList>> result = new ObjectMapper().readValue(rd, HashMap.class);

        rd.close();
        conn.disconnect();

        HashMap<String, ArrayList> res = result.get("msgBody");
        System.out.println(res.get("itemList"));

        ArrayList resArr = res.get("itemList");

        return resArr;

    }

    //버스 노선 정보
    public ArrayList getStaionsByRouteList(String busRouteId) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + "j1X7vFE5RUb9KZAjevsWKZlhE5D3j53C4nqmug7%2Fe8se2gMQgV4Nm6jYuAdHW29ey3Ucn%2FsBwkIScNFRG5jt1g%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("busRouteId","UTF-8") + "=" + URLEncoder.encode(busRouteId, "UTF-8")); /*정류장 고유 ID*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답 유형*/
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        HashMap<String, HashMap<String, ArrayList>> result = new ObjectMapper().readValue(rd, HashMap.class);

        rd.close();
        conn.disconnect();

        HashMap<String, ArrayList> res = result.get("msgBody");
        //System.out.println(res.get("itemList"));

        ArrayList resArr = res.get("itemList");

        return resArr;

    }
}
