package com.error504.baf.service;

import com.error504.baf.Time;
import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.*;
import com.error504.baf.repository.BoardRepository;
import com.error504.baf.repository.QuestionImageRepository;
import com.error504.baf.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;


@Service
public class QuestionService {
    private QuestionRepository questionRepository;
    private QuestionImageRepository questionImageRepository;
    private BoardRepository boardRepository;


    @Autowired
    public QuestionService(QuestionRepository questionRepository, QuestionImageRepository questionImageRepository){
        this.questionRepository = questionRepository;
        this.questionImageRepository = questionImageRepository;
    }


    @Transactional
    public Page<Question> getAllQuestion(int page, String keyword, Long boardId){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        Specification<Question> spec = searchQuestion(keyword, boardId);
        return questionRepository.findAll(spec, pageable);
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

        Collections.reverse(hotList);
        if (hotList.size()>3){
            hotList=hotList.subList(0,4);
            return hotList;
        }
        return hotList;
    }

//    public void deleteVoterById(Long id) {
//
//    }


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
        Collections.reverse(weeklyList);
        if (weeklyList.size()>3){
            weeklyList=weeklyList.subList(0,4);
            return weeklyList;
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
    public Page<Question> getQuestion(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    @Transactional
    public Page<Question> getQuestionResultByUser(Long id, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return questionRepository.findQuestionByAuthorId(pageable, id);
    }

    public List<Question> getQuestionByAuthor(Long id){
        return questionRepository.findQuestionByAuthorId(id);
    }

    public Question getQuestion(Long id){
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()){
            return question.get();
        } else{
            throw new DataNotFoundException("question not found");
        }
    }

    public Long create(String subject, String content, SiteUser user, Board board, Boolean isAnonymous)
    { Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setBoard(board);
        q.setIsAnonymous(isAnonymous);
        questionRepository.saveAndFlush(q);

        return q.getId();
    }

    public void uploadImage(Question question, String path) {
        QuestionImage questionImage = new QuestionImage();
        questionImage.setImage(path);
        questionImage.setQuestion(question);
        questionImageRepository.save(questionImage);
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
        questionRepository.delete(question);
    }

    public void deleteByAuthor(List<Question> questionList) {
        for (Question question : questionList) {
            question.setAuthor(null);
            this.questionRepository.save(question);
        }
    }

    private Specification searchQuestion(String keyword, Long boardId) {
        return (question, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Question, SiteUser> siteUser = question.join("author", JoinType.LEFT);

            Predicate predicate1 = criteriaBuilder.or(
                    criteriaBuilder.like(question.get("subject"), "%" + keyword + "%"),
                    criteriaBuilder.like(question.get("content"), "%" + keyword + "%"),
                    criteriaBuilder.like(siteUser.get("username"),  "%" + keyword + "%")
            );

            if (boardId == 0) {
                return predicate1;
            } else {
                Predicate predicate2 = criteriaBuilder.equal(question.get("board"), boardId);

                return criteriaBuilder.and(predicate1, predicate2);
            }
        };
    }

}
