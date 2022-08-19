package com.error504.baf.controller;

import com.error504.baf.model.Answer;
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


@Controller
public class BoardController {
    private BoardService boardService;
    private UserService userService;

    @Autowired
    public BoardController(BoardService boardService,UserService userService){
        this.boardService = boardService;
        this.userService = userService;
    }

    @RequestMapping("/board/board_list")
    public String bookmarked_list(Model model, @RequestParam(value="page", defaultValue = "0") int page){
        Page<Board> paging = boardService.getList(page);
        model.addAttribute("paging", paging);
        return "community/board_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/board/create")
    public String boardCreate(BoardForm boardForm){
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
        SiteUser siteUser = userService.getUser(principal.getName());
        boardService.create(boardForm.getBoardName(), boardForm.getBoardIntro(), siteUser);
        return "redirect:/board/board_list";
    }



}
