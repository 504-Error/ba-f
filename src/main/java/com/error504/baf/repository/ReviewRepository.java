package com.error504.baf.repository;

import com.error504.baf.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAll(Pageable pageable);
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);

    Page<Review> findReviewByAuthorId(Pageable pageable, Long id);
}
