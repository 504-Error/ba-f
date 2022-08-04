package com.error504.baf.repository;

import com.error504.baf.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findById(Long id);
    Page<Review> findAll(Pageable pageable);
}
