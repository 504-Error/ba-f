package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.BoardService;
import com.error504.baf.service.QuestionService;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final BoardService boardService;
    private final UserService userService;


    @Autowired
    public QuestionController(QuestionService questionService, UserService userService, BoardService boardService) {
        this.questionService = questionService;
        this.userService = userService;
        this.boardService = boardService;

    }

    @RequestMapping("/community/home")
    public String list(Model model, @RequestParam(value="page", defaultValue = "0") int page, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        if(siteUser.getGetAuth()==1 || "admin".equals(siteUser.getUsername())){
        Page<Question> questionList = questionService.getQuestion(page);
        model.addAttribute("questionList", questionList);
        List<Question> weeklyList=questionService.getWeeklyHotList();
        model.addAttribute("weeklyList", weeklyList);
        List<Question> hotList=questionService.getHotList();
        model.addAttribute("hotList", hotList);
        model.addAttribute("tab", "community");

        return "community/question_list";
    }
        Page<Question> questionList = questionService.getQuestion(page);
        model.addAttribute("questionList", questionList);
        List<Question> weeklyList=questionService.getWeeklyHotList();
        model.addAttribute("weeklyList", weeklyList);
        List<Question> hotList=questionService.getHotList();
        model.addAttribute("hotList", hotList);
        model.addAttribute("tab", "community");

    return "community/community_error";
    }


    @RequestMapping("/question/search")
    public String searchList(Model model, @RequestParam(value="page", defaultValue = "0") int page,
                             @RequestParam(value="keyword", defaultValue = "") String keyword){
        //보안 4.1.2
        if (keyword.matches("\\w*") == false) {
            throw new IllegalArgumentException();
        } else {
        Page<Question> questionList = questionService.getAllQuestion(page, keyword, 0L, 0);
        model.addAttribute("questionList", questionList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", "community");
        return "community/question_search";
    }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/hotList")
    public String viewHotList(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> hotList = questionService.getHotQuestion(page);
        model.addAttribute("questionList", hotList);
        model.addAttribute("tab", "community");
        return "community/hot_board";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/weeklyHotList")
    public String viewWeeklyList(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> weeklyList = questionService.getWeeklyQuestion(page);
        model.addAttribute("questionList", weeklyList);
        model.addAttribute("tab", "community");
        return "community/weekly_board";
    }

    @GetMapping(value = "/question/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm) {

        Question question = questionService.getQuestion(id);

        model.addAttribute("answerForm", answerForm);
        model.addAttribute("question", question);
        model.addAttribute("tab", "community");

        return "community/question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create/{id}")
    public String questionCreate(QuestionForm questionForm, @PathVariable("id") Long boardId, Model model) {
        List<Board> board = boardService.findAll();
        model.addAttribute("questionForm", questionForm);
        model.addAttribute("boardId", boardId);
        model.addAttribute("board", board);
        model.addAttribute("tab", "community");
        return "community/question_form";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create/")
    public String questionCreateNull(QuestionForm questionForm, Model model) {
        List<Board> board = boardService.findAll();
        model.addAttribute("questionForm", questionForm);
        model.addAttribute("board", board);
        model.addAttribute("tab", "community");
        return "community/question_form";

    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create/upload")
    @ResponseBody
    public String questionCreate(@RequestPart(name = "contentData") QuestionForm questionForm,
                                 @RequestPart(name = "images", required = false) List<MultipartFile> imageList,
                                 Principal principal) {

        if (questionForm.getBoardId() == 0){
            return "boardIdIsNull";
        } else if ("".equals(questionForm.getSubject())){
            return "subjectIsNull";
        } else if ("".equals(questionForm.getContent())){
            return "contentIsNull";
        }


        SiteUser siteUser = userService.getUser(principal.getName());

        Board board = boardService.getBoard(questionForm.getBoardId());
        Long boardId = questionForm.getBoardId();

        Long id = questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, board, questionForm.getIsAnonymous());

        Path uploadRoot = Paths.get(System.getProperty("user.home")).resolve("baf_storage");
        Path uploadPath;

        if (imageList != null) {
            for (MultipartFile image : imageList) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Timestamp.valueOf(LocalDateTime.now()));
                stringBuilder.append(image.getOriginalFilename());
                String filename = StringUtils.cleanPath(String.valueOf(stringBuilder)); // org.springframework.util
                filename = StringUtils.getFilename(filename);
                filename = filename.replace(":", "-");
                filename = filename.replace(" ", "_");

                uploadPath = uploadRoot.resolve(filename);


                try (InputStream file = image.getInputStream()) {
                    Files.copy(file, uploadPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IllegalStateException("업로드 실패...", e);
                }

                Question question = this.questionService.getQuestion(id);
                this.questionService.uploadImage(question, uploadPath.toString());
            }
        }

        return "/board/question_list/" + boardId;
    }


//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/question/create")
//    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
//                                 Principal principal) {
//        if (bindingResult.hasErrors()) {
//            return "community/question_form";
//        }
//        SiteUser siteUser = userService.getUser(principal.getName());
//        logger.info(questionForm.getBoardId().toString());
//        Board board = boardService.getBoard(questionForm.getBoardId());
//        Long boardId = questionForm.getBoardId();
//        logger.info(siteUser.toString());
//        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, board);
//        return String.format("redirect:/board/question_list/%s", boardId);
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        if(question.getVoter().contains(siteUser)){
            redirectAttributes.addFlashAttribute("message", "이미 좋아요 누른 글입니다.");
            return String.format("redirect:/question/detail/%s", id);
        }
        questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);

    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Long id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/community/home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/accuse/{id}")
    public String questionAccuse(Principal principal, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        if(question.getAccuser().contains(siteUser))
        {
            redirectAttributes.addFlashAttribute("message", "이미 신고 접수가 완료된 글입니다.");
            return String.format("redirect:/question/detail/%s", id);
        }
        questionService.accuse(question, siteUser);
        //신고가 접수되었습니다
        return String.format("redirect:/question/detail/%s", id);

    }
    @RequestMapping("/eventInfo")
    public String index(Model model) {
        model.addAttribute("tab", "community");

        return "community/event_info";

    }





    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/board/question_list/{id}")
    public String viewQuestionList(@PathVariable Long id, Model model, @RequestParam(value="page", defaultValue="0") int page,
                                   @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        //보안 4.1.2
        if (keyword.matches("\\w*") == false) {
            throw new IllegalArgumentException();
        } else {
            Page<Question> questionList = questionService.getAllQuestion(page, keyword, id, 0);
            Board board = boardService.getBoard(id);
            model.addAttribute("questionList", questionList);
            model.addAttribute("board", board);
            model.addAttribute("keyword", keyword);
            model.addAttribute("boardId", id);
            model.addAttribute("tab", "community");

            return "community/board_question";

        }
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/mypage/write")
    public String myPageWrite(Model model, Principal principal, @RequestParam(value="page", defaultValue="0") int page){
        SiteUser siteUser = userService.getUser(principal.getName());
        Page<Question> questionList = questionService.getQuestionResultByUser(siteUser.getId(), page);
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("questionList", questionList);
        model.addAttribute("tab", "mypage");

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

    //


}
