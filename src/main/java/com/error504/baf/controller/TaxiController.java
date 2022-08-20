package com.error504.baf.controller;

import com.error504.baf.service.TaxiService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import java.io.IOException;
import java.util.Map;

@Controller
public class TaxiController {

    private TaxiService taxiService;

    @Autowired
    public TaxiController(TaxiService taxiService) { this.taxiService = taxiService; }

    @GetMapping("/taxi")
    public String setTaxiPage() throws IOException {
//        byte[] test = taxiService.getSMSQR("hello");
//
//        ByteArrayInputStream bis = new ByteArrayInputStream(test);
//        BufferedImage bImage2 = ImageIO.read(bis);
//        ImageIO.write(bImage2, "jpg", new File("C:/Users/woogy/Desktop/test.jpg") );
//        System.out.println("image created");
        return "/baf_map/taxi";
    }

    @GetMapping("/taxi/searchDes")
    public String getDestination() {

        return "/baf_map/searchDes";
    }

    @PostMapping("/taxi/getQR")
    @ResponseBody
    public Response getQR(@RequestBody Map<String, Object> input) throws IOException {
        Response response = new Response();
//        response.setResponse("success");
//        response.setMessage("이미지 조회를 성공적으로 완료했습니다.");
//        response.setData(taxiService.getSMSQR(input.get("MESSAGE").toString()));

        return response;
    }

}
