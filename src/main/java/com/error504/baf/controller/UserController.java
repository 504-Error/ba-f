package com.error504.baf.controller;


import com.error504.baf.model.*;
import com.error504.baf.service.AnnouncementService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private AnnouncementService announcementService;
    private UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, AnnouncementService announcementService) {
        this.announcementService = announcementService;
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

        SiteUser adduser;
        try {
            adduser = userService.create(userCreateForm.getUsername(), userCreateForm.getName(), userCreateForm.getGender(), userCreateForm.getBirthday(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getType(), userCreateForm.getGetWheel());
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "account/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signedFailed", e.getMessage());
            return "account/signup_form";
        }

        if (!userCreateForm.getCertifyFile().isEmpty()) {
            Path uploadRoot = Paths.get(System.getProperty("user.home")).resolve("baf_storage");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Timestamp.valueOf(LocalDateTime.now()));
            stringBuilder.append(userCreateForm.getCertifyFile().getOriginalFilename());
            String filename = StringUtils.cleanPath(String.valueOf(stringBuilder)); // org.springframework.util
            filename = StringUtils.getFilename(filename);
            filename = filename.replace(":", "-");
            filename = filename.replace(" ", "_");

            Path uploadPath = uploadRoot.resolve(filename);

            try (InputStream inputFile = userCreateForm.getCertifyFile().getInputStream()) {
                Files.copy(inputFile, uploadPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalStateException("업로드 실패...", e);
            }

            SiteUser siteUser = this.userService.getUser(adduser.getId());
            this.userService.uploadCertifyFile(siteUser, uploadPath.toString());

        } else {
            // required나 이거 둘 중 하나로 하기
            bindingResult.rejectValue("certifyFile", "certifyFileEmpty",
                    "인증 파일을 첨부해주시길 바랍니다.");
            return "account/signup_form";
        }

        return "redirect:/user/login";
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String myPage(Model model,  Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("tab", "mypage");
        return "account/my_page";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/pwd")
    public String passwordFormCreateNull(PasswordForm passwordForm, Model model){
        model.addAttribute("passwordForm", passwordForm);
        model.addAttribute("tab", "mypage");

        return "account/my_page_pw";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/email")
    public String emailFormCreateNull(EmailForm emailForm, Model model){
        model.addAttribute("emailForm", emailForm);
        model.addAttribute("tab", "mypage");

        return "account/my_page_email";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/delete")
    public String deleteFormCreateNull(PasswordForm passwordForm, Model model){
        model.addAttribute("passwordForm", passwordForm);
        model.addAttribute("tab", "mypage");

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
    @RequestMapping("/mypage/wheelchair")
    public String changeWheelchair(Principal principal) {
        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(String.valueOf(siteUser.getGetWheel()));
        userService.updateWheelchair(siteUser);
        return  "redirect:/user/mypage";

    }

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


//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/mypage/notice")
//    public String myPageNotice(Model model, Principal principal){
//        SiteUser siteUser = userService.getUser(principal.getName());
//        model.addAttribute("siteUser", siteUser);
//        return "account/my_page_notice";
//    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/whatIsBaf")
    public String whatIsBaf(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("tab", "mypage");
        return "what_is_baf";
    }


    @RequestMapping("/mypage/notice")
    public String userAnnounce(Model model, @RequestParam(value="page", defaultValue="0") int page,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        Page<Announcement> announcementPage = announcementService.getList(page, keyword);
        model.addAttribute("announcementPage", announcementPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tab", "mypage");

        return  "announcement_board";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/mypage/notice/{id}")
    public String viewNotice(@PathVariable Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncement(id);
        model.addAttribute("announcement", announcement);
        model.addAttribute("tab", "mypage");
        return "announcement_detail";
    }


}
