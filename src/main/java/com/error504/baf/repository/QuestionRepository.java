package com.error504.baf.repository;

import com.error504.baf.model.Question;
import com.error504.baf.model.SiteUser;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findQuestionById(Long id);
    List<Question> findQuestionByAuthorId(Long id);
    Page<Question> findQuestionByAuthorId(Pageable pageable, Long id);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findQuestionByVoterIsGreaterThanAndCreateDate_Date(Pageable pageable,Long id, LocalDate localDate);
    Page<Question> findQuestionByVoterIsGreaterThanAndCreateDateIsAfter(Pageable pageable,Long id, LocalDate localDate);
    Page<Question> findQuestionByBoardId(Pageable pageable, Long id);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

//    @Query("select u from User u where u.emailAddress = ?1")
}
