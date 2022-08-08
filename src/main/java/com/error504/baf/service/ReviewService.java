package com.error504.baf.service;

import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Review;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public Review getReview(Long id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    public Page<Review> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.reviewRepository.findAll(pageable);
    }

    public void create(String genre, String subject, String date, String place, Integer grade,
                       String amenities, String placeReview, String additionalReview, Boolean isAnonymous, SiteUser user) {
        Review review = new Review();
        review.setGenre(genre);
        review.setSubject(subject);
        review.setDate(date);
        review.setPlace(place);
        review.setGrade(grade);
        review.setAmenities(amenities);
        review.setPlaceReview(placeReview);
        review.setAdditionalReview(additionalReview);
        review.setIsAnonymous(isAnonymous);
        review.setCreateDate(LocalDateTime.now());
        review.setAuthor(user);
        this.reviewRepository.save(review);
    }

    public void vote(Review review, SiteUser siteUser){
        review.getVoter().add(siteUser);
        this.reviewRepository.save(review);
    }

    public void delete(Review review) {
        this.reviewRepository.delete(review);
    }
}
