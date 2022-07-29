package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {

//    @RequestMapping("/sbb")
//    public void main(){
//        System.out.println("gg");
//    }
//
//    @RequestMapping("/layout")
//    public String index() {
//        return "layout";
//    }

    @RequestMapping("/review/main")
    public String reviewMain() {
        return "review_main";
    }

    @RequestMapping("/review/form")
    public String reviewForm() {
        return "review_form";
    }
    @RequestMapping("/review/content")
    public String reviewContent() {
        return "review_content";
    }
}
