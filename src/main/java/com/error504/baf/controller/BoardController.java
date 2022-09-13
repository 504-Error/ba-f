package com.error504.baf.controller;


import com.error504.baf.model.Board;
import com.error504.baf.model.BoardForm;
import com.error504.baf.model.SiteUser;
import com.error504.baf.service.BoardService;
import com.error504.baf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

import static com.error504.baf.SecureFiltering.XssCheck;


@Controller
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @Autowired
    public BoardController(BoardService boardService,UserService userService){
        this.boardService = boardService;
        this.userService = userService;
    }

    @RequestMapping("/board/board_list")
    public String bookmarkedList(Model model, @RequestParam(value="page", defaultValue = "0") int page,
                                  @RequestParam(value="keyword", defaultValue="") String keyword){
        //보안 4.1.2
//        if (keyword.matches("[\\w]*") == false) {
//            throw new IllegalArgumentException();
//        } else {
        Page<Board> paging = boardService.getList(page, keyword);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", "community");
        return "community/board_list";
//    }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/board/create")
    public String boardCreate(Model model, BoardForm boardForm){
        model.addAttribute("boardForm", boardForm);
        return "community/board_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/board/create")
    public String boardCreate(@Valid BoardForm boardForm, BindingResult bindingResult,
                              Principal principal)
    {
        if(bindingResult.hasErrors()) {
            return "community/board_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        boardService.create(XssCheck(boardForm.getBoardName()), XssCheck(boardForm.getBoardIntro()), siteUser);
        return "redirect:/board/board_list";
    }



}
