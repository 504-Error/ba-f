package com.error504.baf.repository;

import com.error504.baf.model.Answer;
import com.error504.baf.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findAnswerByAuthorId(Pageable pageable, Long id);

}
