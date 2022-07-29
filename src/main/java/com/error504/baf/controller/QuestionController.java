package com.error504.baf.controller;

import com.error504.baf.model.AnswerForm;
import com.error504.baf.model.Question;
import com.error504.baf.model.QuestionForm;
import com.error504.baf.model.SiteUser;
import com.error504.baf.service.QuestionService;
import com.error504.baf.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class QuestionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private QuestionService questionService;
    private UserService userService;

    @Autowired
    public QuestionController(QuestionService questionService, UserService userService){
        this.questionService = questionService;
        this.userService = userService;
    }

    @RequestMapping("/question/list")
    public String list(Model model, @RequestParam(value="page", defaultValue = "0") int page){
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @GetMapping(value = "/question/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm){
        logger.info("Viewing Question detail: "+id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create")
    public String questionCreate(QuestionForm questionForm){
        return "question_form";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal)
    {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.toString());
        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Long id) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Long id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        } this.questionService.delete(question);
        return "redirect:/question/list"; }

}
