package com.error504.baf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {
    @RequestMapping("/layout")
    public String index() {
        return "layout";
    }

    @RequestMapping("/visitReview")
    public String visitReview() {
        return "visitReview";
    }
}
