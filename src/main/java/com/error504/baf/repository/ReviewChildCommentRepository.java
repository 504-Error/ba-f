package com.error504.baf.repository;

import com.error504.baf.model.ReviewChildComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewChildCommentRepository extends JpaRepository<ReviewChildComment, Long> {
}
