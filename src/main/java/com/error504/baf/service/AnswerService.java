package com.error504.baf.service;


import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Answer;
import com.error504.baf.model.Question;
import com.error504.baf.model.Review;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public void create(Question question, String content, Boolean isAnonymous, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setIsAnonymous(isAnonymous);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public void deleteByAuthor(List<Answer> answerList) {
        for (Answer answer : answerList) {
            answer.setAuthor(null);
            this.answerRepository.save(answer);
        }
    }

    public Answer getAnswer(Long id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public List<Answer> getAnswerByAuthor(Long id) {
        return this.answerRepository.findAnswerByAuthorId(id);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        answerRepository.save(answer);
    }


    @Transactional
    public Page<Answer> getAnswerResultByUser(Long id, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return answerRepository.findAnswerByAuthorId(pageable, id);
    }

}
