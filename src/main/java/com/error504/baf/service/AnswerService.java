package com.error504.baf.service;


import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Answer;
import com.error504.baf.model.Question;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository){
        this.answerRepository = answerRepository;
    }

    public void create(Question question, String content, Boolean isAnonymous, SiteUser author){
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setIsAnonymous(isAnonymous);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer); }

    public Answer getAnswer(Long id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) { return answer.get();
        } else {
            throw new DataNotFoundException("answer not found"); }


    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        answerRepository.save(answer); }
}
