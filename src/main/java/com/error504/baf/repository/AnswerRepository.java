package com.error504.baf.repository;

import com.error504.baf.model.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAnswerByAuthorId(Long id);
    Page<Answer> findAnswerByAuthorId(Pageable pageable, Long id);

}
