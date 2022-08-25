package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.ReviewCommentService;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            return "review/review_content"; }

        reviewCommentService.create(review, reviewCommentForm.getContent(), reviewCommentForm.getIsAnonymous(), siteUser);
        return String.format("redirect:/review/content/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/{reviewId}/comment/vote/{commentId}")
    public String reviewVote(Principal principal, @PathVariable("reviewId") Long reviewId,
                             @PathVariable("commentId") Long commentId, RedirectAttributes redirectAttributes) {
        ReviewComment reviewComment = this.reviewCommentService.getReviewComment(commentId);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if(reviewComment.getVoter().contains(siteUser)){
            redirectAttributes.addFlashAttribute("message", "이미 좋아요 누른 글입니다.");
            return String.format("redirect:/review/content/%s", reviewId);
        }

        this.reviewCommentService.vote(reviewComment, siteUser);

        return String.format("redirect:/review/content/%s", reviewId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/{reviewId}/comment/delete/{commentId}")
    public String reviewDelete(Principal principal, @PathVariable("reviewId") Long reviewId, @PathVariable("commentId") Long commentId) {
        ReviewComment reviewComment = this.reviewCommentService.getReviewComment(commentId);
        if (!reviewComment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.reviewCommentService.delete(reviewComment);

        return String.format("redirect:/review/content/%s", reviewId);
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
