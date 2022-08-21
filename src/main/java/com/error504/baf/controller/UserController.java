package com.error504.baf.controller;


import com.error504.baf.model.*;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }



    @GetMapping(value="/login")
    public String login(){
        return "account/login_form";
    }



    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "account/signup_form";
    }


    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "account/signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다!");
            return "account/signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getName(), userCreateForm.getGender(), userCreateForm.getBirthday(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getType(), userCreateForm.getGetWheel());
        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "account/signup_form";
        }catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("signedFailed", e.getMessage());
            return "account/signup_form";
        }

        return "redirect:/user/login";
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String myPage(Model model,  Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "account/my_page";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/pwd")
    public String passwordFormCreateNull(PasswordForm passwordForm, Model model){
        model.addAttribute("passwordForm", passwordForm);
        return "account/my_page_pw";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/email")
    public String emailFormCreateNull(EmailForm emailForm, Model model){
        model.addAttribute("emailForm", emailForm);
        return "account/my_page_email";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/delete")
    public String deleteFormCreateNull(PasswordForm passwordForm, Model model){
        model.addAttribute("passwordForm", passwordForm);
        return "account/member_delete";

    }


//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/mypage/comment")
//    public String myPageComment(Model model, Principal principal){
//        SiteUser siteUser = userService.getUser(principal.getName());
//        model.addAttribute("siteUser", siteUser);
//        return "account/my_page_comment";
//    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/settings/email")
    public String emailUpdate( @Valid EmailForm emailForm, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()) {
            return "account/my_page_email";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        userService.updateEmail(siteUser, emailForm.getNewEmail());
        return   "redirect:/user/logout";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/settings/password")
    public String passwordUpdate( @Valid PasswordForm passwordForm, BindingResult bindingResult, Principal principal){

            SiteUser siteUser = userService.getUser(principal.getName());
           if(bindingResult.hasErrors()) {
               return "account/my_page_pw";
           }
           if (!passwordEncoder.matches(passwordForm.getExPassword(), siteUser.getPassword())){
            bindingResult.rejectValue("exPassword", "passwordInCorrect",
                    "잘못된 패스워드를 입력하셨습니다.");
            return "account/my_page_pw";
        }
         if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())){
            bindingResult.rejectValue("newPasswordConfirm", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다!");
            return "account/my_page_pw";
        }
           userService.updatePassword(siteUser, passwordForm.getNewPassword());

           return  "redirect:/user/logout";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/settings/delete")
    public String memberDelete( @Valid PasswordForm passwordForm, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()) {
            return "account/member_delete";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.getPassword());
        logger.info(passwordEncoder.encode(passwordForm.getNewPassword()));
        if(passwordEncoder.matches(passwordForm.getNewPassword(), siteUser.getPassword())){
            userService.deleteMember(siteUser);
        }else{
            return "account/member_delete";
        }
        return   "redirect:/user/logout";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/notice")
    public String myPageNotice(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "account/my_page_notice";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/whatIsBaf")
    public String whatIsBaf(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "what_is_baf";
    }

}
