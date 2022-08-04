package com.error504.baf.controller;

import com.error504.baf.model.Review;
import com.error504.baf.model.ReviewForm;
import com.error504.baf.model.SiteUser;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService){
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @RequestMapping("/review")
    public String reviewMain() {
        return "review/review_main";
    }

    @RequestMapping(value = "/review/content/{id}")
    public String reviewContent(Model model, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "review/review_content";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "review/review_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/review/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "review/review_form";
        }

        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.toString());

        String amenitiesList = String.join(",", reviewForm.getAmenities());

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToString = transFormat.format(reviewForm.getDate());

        reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), dateToString, reviewForm.getPlace(),
                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), reviewForm.getIsAnonymous(), siteUser);

        return "redirect:/review";
    }

    @RequestMapping("/review/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Review> reviewPage = this.reviewService.getList(page);
        model.addAttribute("reviewPage", reviewPage);
        return "review/review_list";
    }
}
