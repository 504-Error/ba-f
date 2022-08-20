package com.error504.baf.repository;

import com.error504.baf.model.Question;
import com.error504.baf.model.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findQuestionById(Long id);
    Page<Question> findQuestionByAuthorId(Pageable pageable, Long id);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findQuestionByBoardId( Pageable pageable, Long id);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
}
