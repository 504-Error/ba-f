package com.error504.baf.service;

import com.error504.baf.model.Review;
import com.error504.baf.model.ReviewChildComment;
import com.error504.baf.model.ReviewComment;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.ReviewChildCommentRepository;
import com.error504.baf.repository.ReviewCommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewChildCommentService {
    private final ReviewChildCommentRepository reviewChildCommentRepository;

    public ReviewChildCommentService(ReviewChildCommentRepository reviewChildCommentRepository){
        this.reviewChildCommentRepository = reviewChildCommentRepository;
    }

    public void create(ReviewComment reviewComment, String content, Boolean isAnonymous, SiteUser user) {
        ReviewChildComment reviewChildComment = new ReviewChildComment();
        reviewChildComment.setContent(content);
        reviewChildComment.setIsAnonymous(isAnonymous);
        reviewChildComment.setCreateDate(LocalDateTime.now());
        reviewChildComment.setReviewComment(reviewComment);
        reviewChildComment.setAuthor(user);
        this.reviewChildCommentRepository.save(reviewChildComment);
    }
}
