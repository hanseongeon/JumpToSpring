package com.example.sbb.question;

import com.example.sbb.answer.Answer;
import com.example.sbb.answer.AnswerForm;
import com.example.sbb.answer.AnswerService;
import com.example.sbb.category.Category;
import com.example.sbb.category.CategoryService;
import com.example.sbb.comment.Comment;
import com.example.sbb.comment.CommentForm;
import com.example.sbb.comment.CommentService;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    @Autowired
    private final QuestionService questionService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AnswerService answerService;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping("/list/{id}")
    public String categoryIdList(@PathVariable("id") int id ,Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getCategoryIdList(page, kw,id);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @ModelAttribute("categories")
    public List<Category> getCategories(){
        return categoryService.getList();
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm, @RequestParam(value = "page", defaultValue = "0") int page, CommentForm commentForm) {
        Question question = this.questionService.getQuestion(id);
        questionService.htiPlus(question);
        model.addAttribute("question", question);
        Page<Answer> paging = this.answerService.getList(page, id);
        model.addAttribute("paging", paging);
        model.addAttribute("commentForm", commentForm);
//        Comment comment = commentService.getCommentByQuestion(question);
//        model.addAttribute("comment",comment);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category = this.categoryService.getCategory(questionForm.getCategory());
        categoryService.save(category);
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
        return "redirect:/question/list";
    }

    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent(),questionForm.getCategory());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    //답변 추천순 정렬
    @GetMapping(value = "/voter/{id}")
    public String vote(Model model, @PathVariable("id") Integer id, AnswerForm answerForm, @RequestParam(value = "page", defaultValue = "0") int page, CommentForm commentForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        Page<Answer> paging = this.answerService.getVoteList(page, id);
        model.addAttribute("paging", paging);
        model.addAttribute("commentForm", commentForm);
        return "question_detail";
    }

//    @GetMapping("/free")
//    public String freeList(Model model, @Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,@RequestParam("category") String category,@RequestParam(value = "page",defaultValue = "0") Integer page) {
//        Page<Question> paging = questionService.getList(page,category);
//        model.addAttribute("paging",paging);
//        return "question_list";
//    }
}

