package com.error504.baf.controller;

import com.error504.baf.model.SiteUser;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {
    private final UserService userService;



    @RequestMapping("")
    public String adminMain(Model model) {
        model.addAttribute("template", "admin/admin_user_registration");

        return "admin/admin_main";
    }

    @RequestMapping("/registration")
    public String adminRegistration(Model model, @RequestParam(value="page", defaultValue="0") int page,
                                    @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<SiteUser> userPage = this.userService.getList(page, keyword);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);

        return "admin/admin_user_registration";
    }
}
