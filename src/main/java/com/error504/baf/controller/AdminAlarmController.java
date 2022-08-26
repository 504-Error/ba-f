package com.error504.baf.controller;


import com.error504.baf.service.OAuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class AdminAlarmController {
    private final OAuthService oAuthService;

    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) {
        System.out.println("code : " + code);
        oAuthService.getKakaoAccessToken(code);
    }
}