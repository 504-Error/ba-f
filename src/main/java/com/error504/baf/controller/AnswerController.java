package com.error504.baf.controller;

import com.error504.baf.model.Answer;
import com.error504.baf.model.AnswerForm;
import com.error504.baf.model.Question;
import com.error504.baf.model.SiteUser;
import com.error504.baf.service.AnswerService;
import com.error504.baf.service.QuestionService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RequestMapping("/answer")

@RequiredArgsConstructor
@Controller
public class AnswerController {
    private QuestionService questionService;
    private AnswerService answerService;
    private UserService userService;

    @Autowired
    public AnswerController(QuestionService questionService, AnswerService answerService,  UserService userService){
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id,
                               @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {

        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "community/question_detail"; }
        answerService.create(question, answerForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/" + id); }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Long id) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다."); }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId()); }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Long id) {
        Answer answer = answerService.getAnswer(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        answerService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

}

