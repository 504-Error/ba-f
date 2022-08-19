package com.error504.baf.service;

import com.error504.baf.Time;
import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.*;
import com.error504.baf.repository.BoardRepository;
import com.error504.baf.repository.QuestionRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


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

//
//    private Specification<Question> search(String kw) {
//        return (review, query, criteriaBuilder) -> {
//            query.distinct(true);
//
//            Predicate predicate1 = criteriaBuilder.or(
//                    criteriaBuilder.like(review.get("subject"), "%" + keyword + "%"),
//                    criteriaBuilder.like(review.get("place"),  "%" + keyword + "%")
//            );
//                query.distinct(true);  // 중복을 제거
//                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
//                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
//                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
//                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
//                        cb.like(q.get("content"), "%" + kw + "%"),
//                        cb.like(u1.get("username"), "%" + kw + "%"),
//                        cb.like(a.get("content"), "%" + kw + "%"),
//                        cb.like(u2.get("username"), "%" + kw + "%"));
//            }
//        };
//    }
    public Page<Question>  getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        Specification<Question> spec = search(kw);
        return questionRepository.findAll(
//                spec,
                pageable);
    }

    @Transactional
    public Page<Question> getQuestionResult(Long id, int page ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findQuestionByBoardId(pageable, id);
    }

    @Transactional
    public Page<Question> getQuestionResultByUser(Long id, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return questionRepository.findQuestionByAuthorId(pageable, id);
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
