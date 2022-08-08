package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.ReviewChildCommentService;
import com.error504.baf.service.ReviewCommentService;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

public class ReviewChildCommentController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;
    private final ReviewChildCommentService reviewChildCommentService;

    public ReviewChildCommentController(UserService userService, ReviewService reviewService, ReviewCommentService reviewCommentService, ReviewChildCommentService reviewChildCommentService){
        this.userService = userService;
        this.reviewService = reviewService;
        this.reviewCommentService = reviewCommentService;
        this.reviewChildCommentService = reviewChildCommentService;
    }

    @PostMapping("/review/childCommend/create/{id}")
    public String createReviewComment(Model model, @PathVariable("id") Long id,
                                      @Valid ReviewChildCommentForm reviewChildCommentForm, BindingResult bindingResult, Principal principal) {

        ReviewComment reviewComment = reviewCommentService.getReviewComment(id);
        Review review = reviewService.getReview(reviewComment.getReview().getId());
        SiteUser siteUser = userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("review", review);
            return "review/review_content"; }

        reviewChildCommentService.create(reviewComment, reviewChildCommentForm.getContent(), reviewChildCommentForm.getIsAnonymous(), siteUser);
        return String.format("redirect:/review/content/%s", id);
    }
}
