package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequestMapping("/management")
@RequiredArgsConstructor
@Controller
public class AdminController {
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;
    private final BoardService boardService;
    private final AnnouncementService announcementService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("")
    public String adminMain(Model model) {
        model.addAttribute("template", "admin/admin_user_management");

        return "admin/admin_main";
    }

    @RequestMapping("/account/{getAuth}")
    public String adminRegistration(Model model, @PathVariable("getAuth") int getAuth,
                                    @RequestParam(value="page", defaultValue="0") int page,
                                    @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<SiteUser> userPage = this.userService.getList(page, keyword, getAuth);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("getAuth", getAuth);

        return "admin/admin_user_management";
    }

    @RequestMapping("/account/{getAuth}/accept/{id}")
    public String adminAccountAccept(Principal principal, @PathVariable("getAuth") int getAuth, @PathVariable("id") Long id) {
        SiteUser siteUser = this.userService.getUser(id);
        // 관리자인지 확인이 필요함!
//        if (!siteUser.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }

        this.userService.updateMemberAuth(siteUser, 1);

        return String.format("redirect:/management/account/%s", getAuth);
    }

    @RequestMapping("/account/{getAuth}/standBy/{id}")
    public String adminAccountStandBy(Principal principal, @PathVariable("getAuth") int getAuth, @PathVariable("id") Long id) {
        SiteUser siteUser = this.userService.getUser(id);
        // 관리자인지 확인이 필요함!
//        if (!siteUser.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }

        this.userService.updateMemberAuth(siteUser, 0);

        return String.format("redirect:/management/account/%s", getAuth);
    }

    @RequestMapping("/account/{getAuth}/delete/{id}")
    public String adminAccountRefuse(Principal principal, @PathVariable("getAuth") int getAuth, @PathVariable("id") Long id) {
        SiteUser siteUser = this.userService.getUser(id);
        // 관리자인지 확인이 필요함!
//        if (!siteUser.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }

        List<Question> questionList = this.questionService.getQuestionByAuthor(id);
        this.questionService.deleteByAuthor(questionList);

        List<Answer> answerList = this.answerService.getAnswerByAuthor(id);
        this.answerService.deleteByAuthor(answerList);

        List<Review> reviewList = this.reviewService.getListByAuthor(id);
        this.reviewService.deleteUser(reviewList);

        List<ReviewComment> reviewCommentList = this.reviewCommentService.getReviewCommentByAuthor(id);
        this.reviewCommentService.deleteByAuthor(reviewCommentList);




        this.userService.deleteMember(siteUser);

        return String.format("redirect:/management/account/%s", getAuth);
    }

    @RequestMapping("/content/{kindOfContent}")
    public String adminManagementContent(Model model,
                                         @PathVariable("kindOfContent") int kindOfContent,
                                         @RequestParam(value="page", defaultValue="0") int page,
                                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                         @RequestParam(value = "boardId", defaultValue = "0") Long boardId) {
        if (kindOfContent == 0) {
            Page<Question> questionPage = this.questionService.getAllQuestion(page, keyword, boardId);
            List<Board> board = boardService.findAll();
            model.addAttribute("contentPage", questionPage);
            model.addAttribute("board", board);
        } if (kindOfContent == 1) {
            Page<Review> reviewPage = this.reviewService.getListWithUsername(page, keyword, boardId.intValue());
            model.addAttribute("contentPage", reviewPage);
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("boardId", boardId);
        model.addAttribute("kindOfContent", kindOfContent);

        return "admin/admin_content_management";
    }

    @RequestMapping("/content/{kindOfContent}/delete/{id}")
    public String adminContentDelete(Principal principal, @PathVariable("kindOfContent") int kindOfContent,
                                     @PathVariable("id") Long id) {
        // 관리자인지 확인이 필요함!
//        if (!siteUser.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }

        if (kindOfContent == 0) {
            Question question = this.questionService.getQuestion(id);
            this.questionService.delete(question);
        } else if (kindOfContent == 1) {
            Review review = this.reviewService.getReview(id);
            this.reviewService.delete(review);
        }

        return String.format("redirect:/management/content/%s", kindOfContent);
    }

    @RequestMapping("/content/{kindOfContent}/detail/{id}")
    public String adminContentDetail(Model model, ReviewCommentForm reviewCommentForm, AnswerForm answerForm,
                                     @PathVariable("kindOfContent") int kindOfContent, @PathVariable("id") Long id) {

        String returnValue = "";

        if (kindOfContent == 0) {
            Question question = questionService.getQuestion(id);
            model.addAttribute("question", question);

            returnValue = "community/question_detail";
        } else if (kindOfContent == 1) {
            Review review = this.reviewService.getReview(id);
            model.addAttribute("review", review);
            switch(review.getGenre()){
                case "음식점":
                    model.addAttribute("category", "restaurant");
                    break;
                case "카페":
                    model.addAttribute("category", "cafe");
                    break;
                case "뮤지컬": case "연극": case "기타 공연":
                    model.addAttribute("category", "perform");
                    break;
                case "기타":
                    model.addAttribute("category", "etc");
                    break;
                default:
                    model.addAttribute("category", "");

            }
            returnValue = "review/review_content";
        }

        return returnValue;
    }

    @RequestMapping("/board")
    public String adminBoard(Model model, @RequestParam(value="page", defaultValue="0") int page,
                                    @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        Page<Board> boardPage = boardService.getList(page, keyword);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("keyword", keyword);

        return "admin/admin_board_management";
    }

    @RequestMapping("/board/delete/{id}")
    public String adminBoardDelete(Model model, @PathVariable("id") Long id) {

        Board board = this.boardService.getBoard(id);
        this.boardService.delete(board);

        return "admin/admin_board_management";
    }

    @RequestMapping("/announce")
    public String adminAnnounce(Model model, @RequestParam(value="page", defaultValue="0") int page,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        Page<Announcement> announcementPage = announcementService.getList(page, keyword);
        model.addAttribute("announcementPage", announcementPage);
        model.addAttribute("keyword", keyword);

        return "admin/admin_announcement_management";
    }

    @RequestMapping("/announce/create")
    public String adminAnnounceCreate(@Valid AnnouncementForm announcementForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "admin/admin_announcement_form";
        }

        Long id = announcementService.create(announcementForm.getSubject(), announcementForm.getSubject());

        return String.format("redirect:/management/announce/%s", id);
    }

    @RequestMapping(value = "/announce/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnnouncementForm announcementForm) {
        Announcement announcement = this.announcementService.getAnnouncement(id);
        model.addAttribute("announcement", announcement);

        return "admin/admin_announcement_detail";
    }

}
