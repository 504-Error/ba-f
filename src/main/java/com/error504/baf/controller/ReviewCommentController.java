package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.ReviewCommentService;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class ReviewCommentController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;

    @Autowired
    public ReviewCommentController(UserService userService, ReviewService reviewService, ReviewCommentService reviewCommentService){
        this.userService = userService;
        this.reviewService = reviewService;
        this.reviewCommentService = reviewCommentService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/review/comment/create/{id}")
    public String createReviewComment(Model model, @PathVariable("id") Long id,
                                      @Valid ReviewCommentForm reviewCommentForm, BindingResult bindingResult, Principal principal) {

        Review review = reviewService.getReview(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("review", review);
            return "review/review_content_2"; }

        reviewCommentService.create(review, reviewCommentForm.getContent(), reviewCommentForm.getIsAnonymous(), siteUser);
        return String.format("redirect:/review/content/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/mypage/comment")
    public String myPageComment(Model model, Principal principal, @RequestParam(value="page", defaultValue="0") int page){
        SiteUser siteUser = userService.getUser(principal.getName());
        Page<ReviewComment> reviewCommentList = reviewCommentService.getReviewCommentResultByUser(siteUser.getId(), page);
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("answerList", reviewCommentList);
        model.addAttribute("tab", "mypage");
        return "account/my_page_comment_review";
    }

}
