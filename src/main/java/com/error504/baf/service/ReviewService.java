package com.error504.baf.service;

import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Review;
import com.error504.baf.model.ReviewImage;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.ReviewImageRepository;
import com.error504.baf.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository){
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    public Review getReview(Long id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    public Page<Review> getReviewByAddress(String address) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(0, 4, Sort.by(sorts));
        Specification<Review> spec = searchReviewByAddress(address);
        return this.reviewRepository.findAll(spec, pageable);
    }

    public Page<Review> getList(int page, String keyword, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        Specification<Review> spec = searchReview(keyword, category);
        return this.reviewRepository.findAll(spec, pageable);
    }

    public List<Review> getListByAuthor(Long id) {
        return this.reviewRepository.findByAuthorId(id);
    }

    public List<Review> getReviewList(){
        List<Review> reviewList = reviewRepository.findAll();
        List<Review> reviewHotList = new ArrayList<>();
        for(Review review:reviewList){
            if((review.getVoter()).size()>2){ //하트 개수 정하기
                if(ChronoUnit.HOURS.between(review.getCreateDate(), LocalDateTime.now())<24){
                    reviewHotList.add(review);
                }
            }
        }

        Collections.reverse(reviewHotList);
        if (reviewHotList.size()>3){
            reviewHotList=reviewHotList.subList(0,4);
            return reviewHotList;
        }
        return reviewHotList;
    }



    public Page<Review> getListWithUsername(int page, String keyword, int categoryId, int sortType) {
        List<Sort.Order> sorts = new ArrayList<>();
        switch (sortType) {
            case 1:
                sorts.add(Sort.Order.desc("voterCount"));
                sorts.add(Sort.Order.desc("createDate"));
                break;
            case 2:
                sorts.add(Sort.Order.desc("accuserCount"));
                sorts.add(Sort.Order.desc("createDate"));
                break;
            default:
                sorts.add(Sort.Order.desc("createDate"));
        }
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        Specification<Review> spec = searchReviewWithUsername(keyword, categoryId);
        return this.reviewRepository.findAll(spec, pageable);
    }

    public Long create(String genre, String subject, String date, String place, String placeAddress, Integer grade,
                       String amenities, String placeReview, String additionalReview, Boolean isAnonymous, SiteUser user) {
        Review review = new Review();
        review.setGenre(genre);
        review.setSubject(subject);
        review.setDate(date);
        review.setPlace(place);
        review.setPlaceAddress(placeAddress);
        review.setGrade(grade);
        review.setAmenities(amenities);
        review.setPlaceReview(placeReview);
        review.setAdditionalReview(additionalReview);
        review.setIsAnonymous(isAnonymous);
        review.setCreateDate(LocalDateTime.now());
        review.setAuthor(user);
        this.reviewRepository.save(review);
        this.reviewRepository.flush();
        long id = review.getId();


        return id;
    }

    public void uploadImage(Review review, String path) {
        ReviewImage reviewImage = new ReviewImage();
//        reviewImage.setImageNUm(num);
        reviewImage.setImage(path);
        reviewImage.setReview(review);
        this.reviewImageRepository.save(reviewImage);
    }

    public void vote(Review review, SiteUser siteUser){
        review.getVoter().add(siteUser);
        this.reviewRepository.save(review);
    }

    public void accuse(Review review, SiteUser siteUser){
        review.getAccuser().add(siteUser);
        this.reviewRepository.save(review);
    }

    public void delete(Review review) {
        this.reviewRepository.delete(review);
    }

    public void deleteUser(List<Review> reviewList) {
        for (Review review : reviewList) {
            review.setAuthor(null);
            this.reviewRepository.save(review);
        }
    }

    private Specification searchReview(String keyword, String category){
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
                    categories.add("기타 공연");
                    break;
                case "etc":
                    categories.add("기타");
                    break;
                default:
                    categories.add("음식점");
                    categories.add("카페");
                    categories.add("뮤지컬");
                    categories.add("연극");
                    categories.add("기타 공연");
                    categories.add("기타");
            }

            Predicate predicate1 = criteriaBuilder.or(
                    criteriaBuilder.like(review.get("genre"), "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("subject"), "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("place"),  "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("placeAddress"),  "%" + keyword + "%")
            );

            Predicate predicate2 = criteriaBuilder.and(review.get("genre").in(categories));


            return criteriaBuilder.and(predicate1, predicate2);
        };
    }

    private Specification searchReviewWithUsername(String keyword, int categoryId){
        return (review, query, criteriaBuilder) -> {
            query.distinct(true);

            List<String> categories = new ArrayList<>();

            switch(categoryId){
                case 1:
                    categories.add("음식점");
                    break;
                case 2:
                    categories.add("카페");
                    break;
                case 3:
                    categories.add("뮤지컬");
                    break;
                case 4:
                    categories.add("연극");
                    break;
                case 5:
                    categories.add("기타 공연");
                    break;
                case 6:
                    categories.add("기타");
                    break;
                default:
                    categories.add("음식점");
                    categories.add("카페");
                    categories.add("뮤지컬");
                    categories.add("연극");
                    categories.add("기타 공연");
                    categories.add("기타");
            }

            Join<Review, SiteUser> siteUser = review.join("author", JoinType.LEFT);

            Predicate predicate1 = criteriaBuilder.or(
                    criteriaBuilder.like(review.get("subject"), "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("place"),  "%" + keyword + "%"),
                    criteriaBuilder.like(review.get("placeAddress"),  "%" + keyword + "%"),
                    criteriaBuilder.like(siteUser.get("username"),  "%" + keyword + "%")
            );

            Predicate predicate2 = criteriaBuilder.and(review.get("genre").in(categories));


            return criteriaBuilder.and(predicate1, predicate2);
        };
    }

    private Specification searchReviewByAddress(String address){
        return (review, query, criteriaBuilder) -> {
            query.distinct(true);

            return criteriaBuilder.equal(review.get("placeAddress"),  address);
        };
    }


    public Page<Review> getReviewResult(Long id, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return reviewRepository.findReviewByAuthorId(pageable, id);
    }
}