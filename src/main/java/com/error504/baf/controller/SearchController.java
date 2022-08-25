package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.repository.ReviewRepository;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @GetMapping("/search")
    public String test() {
        return "/baf_map/search";
    }
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public SearchController(ReviewService reviewService, UserService userService){
        this.reviewService = reviewService;
        this.userService = userService;
    }



    @RequestMapping(value = "/content/{id}")
    public String detail(Model model, @PathVariable("id") Long id, ReviewCommentForm reviewCommentForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        if (review.getPlace() == "서울 노원구 화랑로 441"){
            model.addAttribute("id", id);
        }
        return "/baf_map/search";
    }

    @RequestMapping("")
    public String reviewMain(Model model, @RequestParam(value="page", defaultValue="0") int page,
                             @RequestParam(value = "kw", defaultValue = "") String keyword) {
        Page<Review> reviewPage = this.reviewService.getList(page, keyword, "");
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("keyword", keyword);
        return "/baf_map/search";
    }

}