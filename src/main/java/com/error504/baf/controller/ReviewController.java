package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.error504.baf.controller.ReviewSearchPerformController.getPerformData;

@RequestMapping("/review")
@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService){
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @RequestMapping("")
    public String reviewMain(Model model, @RequestParam(value="page", defaultValue="0") int page,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        // 카테고리 없이 카테고리 자체도 검색 키워드가 될 수 있도록...!
        Page<Review> reviewPage = this.reviewService.getList(page, keyword, "");
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("keyword", keyword);
        return "review/review_main";
    }

    @RequestMapping("/{category}")
    public String reviewMain(Model model, @RequestParam(value="page", defaultValue="0") int page,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @PathVariable("category") String category) {
        logger.info("category : " + category);
        Page<Review> reviewPage = this.reviewService.getList(page, keyword, category);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        return "review/review_list";
    }

    @RequestMapping(value = "/content/{id}")
    public String detail(Model model, @PathVariable("id") Long id, ReviewCommentForm reviewCommentForm) {
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
        return "review/review_content";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(Model model, ReviewForm reviewForm) {
        return "review/review_form";
    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/create")
//    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
//        if (bindingResult.hasErrors()) {
//            return "review/review_form";
//        }
//
//
//        SiteUser siteUser = userService.getUser(principal.getName());
//        logger.info(siteUser.toString());
//
//        String amenitiesList = String.join(",", reviewForm.getAmenities());
//
//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String dateToString = transFormat.format(reviewForm.getDate());
//
//        reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), dateToString, reviewForm.getPlace(),
//                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), reviewForm.getIsAnonymous(), siteUser);
//
//        return "redirect:/review";
//    }

    // 뭔가 예외처리가 필요할 것 같음
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/upload")
    @ResponseBody
    public Long reviewUpload(
            @Valid @RequestPart(name = "reviewData") ReviewForm reviewForm,
            @RequestPart(name = "images", required = false) List<MultipartFile> imageList,
            BindingResult bindingResult, Principal principal, HttpServletRequest request) throws IOException {
//        if (bindingResult.hasErrors()) {
//            logger.info("bindingResult.hasErrors()");
//            return "review/review_form";
//        }

        logger.info("no binding error");
        logger.info("reviewForm : " + reviewForm);
        logger.info("reviewForm.getGenre() : " + reviewForm.getGenre());
        if (imageList.size() > 0) {
            logger.info("imageList : " + imageList.get(0));
        }

//        logger.info("imgUrl ArrayList : ", reviewForm.getImageUrl());
//
        SiteUser siteUser = userService.getUser(principal.getName());
        logger.info(siteUser.toString());

        String amenitiesList = String.join(",", reviewForm.getAmenities());

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToString = transFormat.format(reviewForm.getDate());

        Long id = reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), dateToString, reviewForm.getPlace(), reviewForm.getPlaceAddress(),
                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), reviewForm.getIsAnonymous(), siteUser);

        logger.info("review id : " + id);

        String uploadRoot = Paths.get(System.getProperty("user.home")).resolve("baf_image").toString();

        for (int i = 0; i < imageList.size(); i++){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/");
            stringBuilder.append(Timestamp.valueOf(LocalDateTime.now()));
            stringBuilder.append(imageList.get(i).getOriginalFilename());

            File path = new File(uploadRoot + stringBuilder);
            try {
                imageList.get(i).transferTo(path);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Review review = this.reviewService.getReview(id);
            logger.info("path.toString" + path.toString());
            this.reviewService.uploadImage(review, path.toString());
        }

        return id;
    }

//    @RequestMapping("/list")
//    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
//                       @RequestParam(value = "kw", defaultValue = "") String keyword) {
//        Page<Review> reviewPage = this.reviewService.getList(page, keyword);
//        model.addAttribute("reviewPage", reviewPage);
//        model.addAttribute("keyword", keyword);
//        logger.info("reviewPage : ", reviewPage, ", keyword : ", keyword);
//        return "review/review_list";
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String reviewVote(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.vote(review, siteUser);
        return String.format("redirect:/review/content/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String reviewDelete(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.reviewService.delete(review);
        return "redirect:/review";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/accuser/{id}")
    public String reviewAccuser(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.reviewService.accuse(review, siteUser);
        return String.format("redirect:/review/content/%s", id);
    }

    @GetMapping("/create/search/place/*")
    public String searchPlace() {
        return "review/review_search_place";
    }

    @GetMapping("/create/search/perform")
    public String searchShow(Model model, @RequestParam(value="genre", defaultValue="0") int genre,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        ArrayList<ReviewPerformInfo> performInfoList = getPerformData(genre, keyword);

        model.addAttribute("performInfoList", performInfoList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);

        return "review/review_search_perform";
    }
}
