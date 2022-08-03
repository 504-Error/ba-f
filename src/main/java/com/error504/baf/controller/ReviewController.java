package com.error504.baf.controller;

import com.error504.baf.model.Review;
import com.error504.baf.model.ReviewForm;
import com.error504.baf.model.SiteUser;
import com.error504.baf.service.QuestionService;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
public class ReviewController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private UserService userService;
    private ReviewService reviewService;

    @RequestMapping("/review")
    public String reviewMain() {
        return "review/review_main";
    }

    @RequestMapping("/review/content")
    public String reviewContent() {
        return "review/review_content";
    }

    @GetMapping("/review/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "review/review_form";
    }

    @PostMapping("/review/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "review/review_form";
        }

        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.toString());

        String amenitiesList = String.join("", reviewForm.getAmenities());

        reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), reviewForm.getDate(), reviewForm.getPlace(),
                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), siteUser);

        return "redirect:/review";
    }

    @RequestMapping("/review/list")
    public String reviewList(Model model) {
        List<Review> reviewList = this.reviewService.getList();
        model.addAttribute("reviewList", reviewList);
        return "review_list";
    }
}
