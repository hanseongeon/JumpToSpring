package com.example.sbb.user;

import com.example.sbb.answer.Answer;
import com.example.sbb.comment.Comment;
import com.example.sbb.email.EmailService;
import com.example.sbb.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final EmailService emailService;


    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${temp.password.length}")
    private int tempPasswordLength;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {

        return "/login_form";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(UserPasswordForm userPasswordForm) {
        return "forgot_password_form";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, @Valid UserPasswordForm userPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "아이디와 이메일을 다시 확인해주세요.");
            return "forgot_password_form";
        }
        SiteUser siteUser = userService.findByEmail(userPasswordForm.getEmail());
        if (!siteUser.getUsername().equals(userPasswordForm.getUsername())) {
            model.addAttribute("error", "이메일이 올바르지 않습니다.");
            return "forgot_password_form";
        } else if (siteUser == null) {
            model.addAttribute("error", "존재하지 않는 아이디입니다.");
            return "forgot_password_form";
        }

        // 임시 비밀번호 생성
        String tempPassword = this.userService.generateTempPassword(tempPasswordLength);

        // 임시 비밀번호 저장
        siteUser.setPassword(passwordEncoder.encode(tempPassword));
        this.userService.temp_save(siteUser);

        // 이메일 전송
        emailService.sendEmail(siteUser.getEmail(), "임시 비밀번호 발급", "임시 비밀번호: " + tempPassword);

        model.addAttribute("message", "이메일로 임시 비밀번호가 전송되었습니다.");
        return "redirect:/question/list";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        SiteUser siteUser = this.userService.findByUserName(principal.getName());
        Page<Question> paging = this.userService.getPagedQuestionsByUser(siteUser, page);
        model.addAttribute("paging", paging);
        model.addAttribute("siteUser", siteUser);
        if(!model.containsAttribute("url") || model.getAttribute("url")==null)
            model.addAttribute("url",siteUser.getProfile_image());
        return "user_profile";
    }

    @GetMapping("/profileAnswer")
    public String profileAnswer(Principal principal, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        SiteUser siteUser = this.userService.findByUserName(principal.getName());
        Page<Answer> paging = this.userService.getPagedAnswerByUser(siteUser, page);
        model.addAttribute("paging", paging);
        model.addAttribute("siteUser", siteUser);
        return "user_answer_profile";
    }

    @GetMapping("/profileComment")
    public String profileComment(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        SiteUser siteUser = this.userService.findByUserName(principal.getName());
        Page<Comment> paging = this.userService.getPagedCommentByUser(siteUser, page);
        model.addAttribute("paging", paging);
        model.addAttribute("siteUser", siteUser);
        return "user_comment_profile";
    }

    @PostMapping("/imageform")
    public String imageform(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes,@RequestParam("text") String text){
        String url = null;
        if(file.getContentType().contains("image"))
            url = userService.temp_save(file);
        redirectAttributes.addFlashAttribute("url",url);
        redirectAttributes.addFlashAttribute("text",text);
        return "redirect:/user/profile";
    }

    @PostMapping("/imagesaveform")
    public String imagesaveform(Principal principal, @RequestParam(value = "url",defaultValue = "")String url){
        if(url.isBlank())
            return "redirect:/user/profile";
        SiteUser siteUser = userService.getUser(principal.getName());
        userService.save(siteUser,url);
        return "redirect:/user/profile";
    }

}