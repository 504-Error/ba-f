package com.error504.baf.service;

import com.error504.baf.Time;
import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Board;
import com.error504.baf.model.Question;
import com.error504.baf.model.Review;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.BoardRepository;
import com.error504.baf.repository.QuestionRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private QuestionRepository questionRepository;
    private BoardRepository boardRepository;


    @Autowired
    public QuestionService(QuestionRepository questionRepository, BoardRepository boardRepository){
        this.questionRepository = questionRepository;
    }



    public Page<Question> getList(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }
    public List<Question> getHotList(){
        List<Question> questionList = new ArrayList<>();
        questionList = questionRepository.findAll();
        List<Question> hotList = new ArrayList<>();
        for(Question question:questionList){
            if((question.getVoter()).size()>2){ //하트 개수 정하기
               if(ChronoUnit.HOURS.between(question.getCreateDate(), LocalDateTime.now())<24){
                    hotList.add(question);
                }
            }
        }
        return hotList;
    }


    public List<Question> getWeeklyHotList(){
        List<Question> questionNewList = new ArrayList<>();
        questionNewList = questionRepository.findAll();
        List<Question> weeklyList = new ArrayList<>();
        for(Question question:questionNewList){
            if((question.getVoter()).size()>2){ //하트 개수 정하기

                if(Time.getWeekOfYear(question.getCreateDate().toLocalDate().toString())==Time.getWeekOfYear(LocalDate.now().toString())){
                    weeklyList.add(question);
                }

            }
        }
        return weeklyList;
    }


    public Page<Question>  getQuestionList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    @Transactional
    public List<Question> getQuestionResult(Long id, int page ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findQuestionByBoardId(id, pageable);
    }

    public Question getQuestion(Long id){
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()){
            return question.get();
        } else{
            throw new DataNotFoundException("question not found");
        }
    }


    public void create(String subject, String content, SiteUser user, Board board)
    { Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setBoard(board);
        questionRepository.save(q);
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        questionRepository.save(question);
    }
    public void accuse(Question question, SiteUser siteUser){
        question.getAccuser().add(siteUser);
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question); }


}
