package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.BoardService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class QuestionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private QuestionService questionService;
    private BoardService boardService;
    private UserService userService;

    @Autowired
    public QuestionController(QuestionService questionService, UserService userService, BoardService boardService){
        this.questionService = questionService;
        this.userService = userService;
        this.boardService = boardService;
    }

    @RequestMapping("/question/list")
    public String list(Model model, @RequestParam(value="page", defaultValue = "0") int page){
        Page<Question> questionList = questionService.getQuestion(page);
        model.addAttribute("questionList", questionList);
        List<Question> weeklyList=questionService.getWeeklyHotList();
        model.addAttribute("weeklyList", weeklyList);
        List<Question> hotList=questionService.getHotList();
        model.addAttribute("hotList", hotList);

        return "community/question_list";
    }

    @RequestMapping("/question/search")
    public String searchList(Model model, @RequestParam(value="page", defaultValue = "0") int page){
        Page<Question> questionList = questionService.getQuestion(page);
        model.addAttribute("questionList", questionList);
        return "community/question_search";
    }

    @PreAuthorize("isAuthenticated()")
     @GetMapping("/question/hotList")
    public String hotList(Model model) {
         List<Question> hotList=questionService.getHotList();
        model.addAttribute("hotList", hotList);
        return "community/hot_board";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/weeklyHotList")
    public String weeklyHotList(Model model) {
        List<Question> weeklyList=questionService.getWeeklyHotList();
        model.addAttribute("weeklyList", weeklyList);
        return "community/weekly_board";
    }

    @GetMapping(value = "/question/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm){
        logger.info("Viewing Question detail: "+id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "community/question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create/{id}")
    public String questionCreate(QuestionForm questionForm,  @PathVariable("id") Long boardId, Model model){
        List<Board> board = boardService.findAll();
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
        return "community/question_form";

    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create/")
    public String questionCreateNull(QuestionForm questionForm, Model model){
        List<Board> board = boardService.findAll();
        model.addAttribute("board", board);
        return "community/question_form";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal)
    {
        if(bindingResult.hasErrors()) {
            return "community/question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(questionForm.getBoardId().toString());
        Board board = boardService.getBoard(questionForm.getBoardId());
        Long boardId = questionForm.getBoardId();
        logger.info(siteUser.toString());
        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, board);
        return String.format("redirect:/board/question_list/%s", boardId);
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/accuse/{id}")
    public String questionAccuse(Principal principal, @PathVariable("id") Long id) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.accuse(question, siteUser);
      //신고가 접수되었습니다
        return String.format("redirect:/question/detail/%s", id);

    }

    @RequestMapping("/eventInfo")
    public String index() {
        return "community/event_info";
    }





    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/board/question_list/{id}")
    public String viewQuestionList(@PathVariable Long id, Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> questionList = questionService.getQuestionResult(id,page);
        Board board = boardService.getBoard(id);
        model.addAttribute("questionList", questionList);
        model.addAttribute("board", board);
        return "community/board_question";
    }

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
        @RequestParam(value="kw", defaultValue="") String kw){
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "communuity/board_question";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/mypage/write")
    public String myPageWrite(Model model, Principal principal, @RequestParam(value="page", defaultValue="0") int page){
        SiteUser siteUser = userService.getUser(principal.getName());
        Page<Question> questionList = questionService.getQuestionResultByUser(siteUser.getId(), page);
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("questionList", questionList);
        return "account/my_page_write";
    }




//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/user/mypage/like")
//    public String myPageLike(Model model, Principal principal, @RequestParam(value="page", defaultValue="0") int page){
//        SiteUser siteUser = userService.getUser(principal.getName());
//        Question <Question>
//        if(question.getVoter().contains(siteUser))
//
//            this.reviewRepository.save(review);
//        }
//
//        List <Long> questionId= new ArrayList<>();
//        if(set)
//        questionId.add
//
//        Page<Question> questionList = questionService.getQuestionResultByUser(siteUser.getId(), page);
//        model.addAttribute("siteUser", siteUser);
//        model.addAttribute("questionList", questionList);
//        return "account/my_page_like";
//    }
}
