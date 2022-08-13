package com.error504.baf.controller;

import com.error504.baf.model.*;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.error504.baf.controller.ReviewSearchPerform.getPerformData;

@RequestMapping("/review")
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

    @RequestMapping("")
    public String reviewMain() {
        return "review/review_main";
    }

    @RequestMapping(value = "/content/{id}")
    public String detail(Model model, @PathVariable("id") Long id, ReviewCommentForm reviewCommentForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "review/review_content";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "review/review_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "review/review_form";
        }

        logger.info("imgUrl ArrayList : ", reviewForm.getImageUrl());

        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.toString());

        String amenitiesList = String.join(",", reviewForm.getAmenities());

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToString = transFormat.format(reviewForm.getDate());

        reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), dateToString, reviewForm.getPlace(),
                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), reviewForm.getIsAnonymous(), siteUser);

        return "redirect:/review";
    }

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Review> reviewPage = this.reviewService.getList(page);
        model.addAttribute("reviewPage", reviewPage);
        return "review/review_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.vote(review, siteUser);
        return String.format("redirect:/review/content/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.reviewService.delete(review);
        return "redirect:/";
    }

    @GetMapping("/create/search/place")
    public String searchPlace() {
        return "review/review_search_place";
    }

    @GetMapping("/create/search/perform")
    public String searchShow(Model model) {
        ArrayList<ReviewPerformInfo> performInfoList = getPerformData();

        model.addAttribute("performInfoList", performInfoList);

        return "review/review_search_perform";
    }
}
