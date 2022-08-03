package com.error504.baf.service;

import com.error504.baf.model.Review;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getList() {
        return this.reviewRepository.findAll();
    }

    public void create(String genre, String subject, Date date, String place, Integer grad,
                       String amenities, String placeReview, String additionalReview, SiteUser user) {
        Review review = new Review();
        review.setGenre(genre);
        review.setSubject(subject);
        review.setDate(date);
        review.setPlace(place);
        review.setGrade(grad);
        review.setAmenities(amenities);
        review.setPlaceReview(placeReview);
        review.setAdditionalReview(additionalReview);
        review.setCreateDate(LocalDateTime.now());
        review.setAuthor(user);
        this.reviewRepository.save(review);
    }

    public void vote(Review review, SiteUser siteUser){
        review.getVoter().add(siteUser);
        reviewRepository.save(review);
    }

    public void delete(Review review) {
        reviewRepository.delete(review);
    }
}
