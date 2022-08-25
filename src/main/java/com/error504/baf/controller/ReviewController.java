package com.error504.baf.controller;

import com.error504.baf.model.*;
import com.error504.baf.service.ReviewService;
import com.error504.baf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        Page<Review> reviewPage = this.reviewService.getList(page, keyword, "");
        List<Review> reviewList = this.reviewService.getReviewList();

        model.addAttribute("tab", "review");
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("category", "all");
        model.addAttribute("keyword", keyword);
        return "review/review_main";
    }

    @RequestMapping("/{category}/{type}")
    public String reviewMain(Model model, @RequestParam(value="page", defaultValue="0") int page,
                             @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @PathVariable("category") String category,
                             @PathVariable("type") String type,
                             HttpServletResponse response) throws IOException {
        Page<Review> reviewPage = this.reviewService.getList(page, keyword, category);

        if (reviewPage.getSize() == 0) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('검색 결과가 없습니다.'); history.go(-1);</script>");
            out.flush();
        }

        model.addAttribute("tab", "review");
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("type", type);
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
                model.addAttribute("category", "all");

        }

        model.addAttribute("tab", "review");

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
    public String reviewUpload(
            @RequestPart(name = "reviewData") ReviewForm reviewForm,
            @RequestPart(name = "images", required = false) List<MultipartFile> imageList,
            BindingResult bindingResult, Principal principal, HttpServletRequest request) throws IOException {

        if ("0".equals(reviewForm.getGenre())){
            return "genreIsNull";
        } else if ("".equals(reviewForm.getSubject())) {
            return "subjectIsNull";
        } else if ("".equals(reviewForm.getPlace())) {
            return "placeIsNull";
        } else if (reviewForm.getGrade() == 0) {
            return "gradeIsNull";
        } else if ("".equals(reviewForm.getPlaceReview())) {
            return "placeReviewIsNull";
        }

        SiteUser siteUser = userService.getUser(principal.getName());

        String amenitiesList = String.join(",", reviewForm.getAmenities());

        String dateToString = "알 수 없음";
        if (reviewForm.getDate() != null) {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateToString = transFormat.format(reviewForm.getDate());
        }

        Long id = reviewService.create(reviewForm.getGenre(), reviewForm.getSubject(), dateToString, reviewForm.getPlace(), reviewForm.getPlaceAddress(),
                reviewForm.getGrade(), amenitiesList, reviewForm.getPlaceReview(), reviewForm.getAdditionalReview(), reviewForm.getIsAnonymous(), siteUser);

        if (id == -1) {
            return "errorImage";
        }

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

                Review review = this.reviewService.getReview(id);
                this.reviewService.uploadImage(review, uploadPath.toString());
            }
        }

        String category = "";
        switch (reviewForm.getGenre()) {
            case "음식점":
                category = "restaurant";
                break;
            case "카페":
                category = "cafe";
                break;
            case "뮤지컬": case "연극": case "기타 공연":
                category = "perform";
                break;
            case "기타":
                category = "etc";
                break;
            default:
                category = "";
        }

        return "/review/" + category + "/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String reviewVote(Principal principal, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if(review.getVoter().contains(siteUser)){
            redirectAttributes.addFlashAttribute("message", "이미 좋아요 누른 글입니다.");
            return String.format("redirect:/review/content/%s", id);
        }
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/mypage/write")
    public String myPageWriteReview(Model model, Principal principal, @RequestParam(value="page", defaultValue="0") int page){
        SiteUser siteUser = userService.getUser(principal.getName());
        Page<Review> reviewPage = reviewService.getReviewResult(siteUser.getId(), page);

        model.addAttribute("tab", "mypage");
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("reviewPage", reviewPage);
        return "account/my_page_write_review";
    }
}
