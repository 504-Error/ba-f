package com.error504.baf.controller;

import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController {
    private final ReviewService reviewService;

    @Autowired
    public SearchController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping("/search")
    public String searchPlace(Model model) {
        model.addAttribute("tab", "map");

        return "/baf_map/search";
    }

    @PostMapping("/search/loadReview")
    @ResponseBody
    public Object searchPlaceReview(@RequestBody String address) {
        if ("".equals(address)){
            return "error";
        }

        return this.reviewService.getReviewByAddress(address);
    }

}
