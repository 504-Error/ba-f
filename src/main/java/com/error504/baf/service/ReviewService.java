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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    public Page<Review> getList(int page, String keyword, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 4, Sort.by(sorts));
        Specification<Review> spec = reviewSearch(keyword, category);
        return this.reviewRepository.findAll(spec, pageable);
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

    private Specification reviewSearch(String keyword, String category){
        return (review, query, criteriaBuilder) -> {
            query.distinct(true);

            List<String> categories = new ArrayList<>();

            switch(category){
                case "restaurant":
                    categories.add("음식점");
                    break;
                case "cafe":
                    categories.add("카페");
                    break;
                case "perform":
                    categories.add("뮤지컬");
                    categories.add("연극");
                    categories.add("전시관");
                    break;
                case "etc":
                    categories.add("기타");
                    break;
                default:
                    categories.add("음식점");
                    categories.add("카페");
                    categories.add("뮤지컬");
                    categories.add("연극");
                    categories.add("전시관");
                    categories.add("기타");
            }

            logger.info("service - category : " + category);
            logger.info("service - categories : " + categories);
            logger.info("service - keyword : " + keyword);

            Predicate predicate1 = criteriaBuilder.or(
                    criteriaBuilder.like(review.get("subject"), "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("place"),  "%" + keyword + "%")
            );

            Predicate predicate2 = criteriaBuilder.and(review.get("genre").in(categories));


            return criteriaBuilder.and(predicate1, predicate2);
        };
    }
}