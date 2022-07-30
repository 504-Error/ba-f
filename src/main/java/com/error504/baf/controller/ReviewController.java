package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ReviewController {

//    @RequestMapping("/sbb")
//    public void main(){
//        System.out.println("gg");
//    }
//
//    @RequestMapping("/layout")
//    public String index() {
//        return "layout";
//    }

    @RequestMapping("/review")
    public String reviewMain() {
        return "review/review_main";
    }

    @RequestMapping("/review/write")
    public String reviewForm() {
        return "review/review_form";
    }
    @RequestMapping("/review/content")
    public String reviewContent() {
        return "review/review_content";
    }
}
