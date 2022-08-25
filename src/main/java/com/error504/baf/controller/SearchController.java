package com.error504.baf.controller;

import com.error504.baf.model.Review;
import com.error504.baf.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    public ArrayList searchPlaceReview(@RequestBody String address) {
        Page<Review> page = this.reviewService.getReviewByAddress(address);

        ArrayList<Page<Review>> reviewPage = new ArrayList<>();
        reviewPage.add(page);
        return reviewPage;
    }
}