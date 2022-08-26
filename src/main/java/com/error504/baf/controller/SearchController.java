package com.error504.baf.controller;

import com.error504.baf.model.Review;
import com.error504.baf.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public List<Review> searchPlaceReview(@RequestBody String address) {
        List<Review> reviewList = this.reviewService.getReviewByAddress(address);

//        System.out.println("reviewList size : " + reviewList.size());
//        System.out.println("reviewList size : " + reviewList);

        return reviewList;
    }
}