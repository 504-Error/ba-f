package com.error504.baf.repository;

import com.error504.baf.model.Answer;
import com.error504.baf.model.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    Page<ReviewComment> findReviewCommentByAuthorId(Pageable pageable, Long id);

}
