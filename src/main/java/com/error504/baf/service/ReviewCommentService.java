package com.error504.baf.service;

import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Review;
import com.error504.baf.model.ReviewComment;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.ReviewCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@Service
public class ReviewCommentService {
    private final ReviewCommentRepository reviewCommentRepository;

    public ReviewCommentService(ReviewCommentRepository reviewCommentRepository){
        this.reviewCommentRepository = reviewCommentRepository;
    }

    public ReviewComment getReviewComment(Long id) {
        Optional<ReviewComment> review = this.reviewCommentRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    public List<ReviewComment> getReviewCommentByAuthor(Long id) {
        return this.reviewCommentRepository.findByAuthorId(id);
    }

    public void create(Review review, String content, Boolean isAnonymous, SiteUser user) {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setContent(content);
        reviewComment.setIsAnonymous(isAnonymous);
        reviewComment.setCreateDate(LocalDateTime.now());
        reviewComment.setReview(review);
        reviewComment.setAuthor(user);
        this.reviewCommentRepository.save(reviewComment);
    }



    public Page<ReviewComment> getReviewCommentResultByUser(Long id, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return reviewCommentRepository.findReviewCommentByAuthorId(pageable, id);
    }


    public void delete(ReviewComment reviewComment) {
        reviewCommentRepository.delete(reviewComment);
    }

    public void deleteByAuthor(List<ReviewComment> reviewCommentList) {
        for (ReviewComment reviewComment : reviewCommentList) {
            reviewComment.setAuthor(null);
            this.reviewCommentRepository.save(reviewComment);
        }
    }
}
