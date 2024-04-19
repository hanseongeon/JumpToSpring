package com.example.sbb.comment;

import com.example.sbb.answer.Answer;
import com.example.sbb.answer.AnswerService;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionService;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;
    @Autowired
    private final QuestionService questionService;
    @Autowired
    private final AnswerService answerService;
    @Autowired
    private final UserService userService;

    @PostMapping("/createQuestion/{id}")
    public String createQuestionComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("commentForm", commentForm);
            return "/question/detail";
        }
        Comment comment = commentService.createComment(question, commentForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestionComment().getId(), comment.getId());

    }

    @PostMapping("/createAnswer/{id}")
    public String createAnswerComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answer);
            model.addAttribute("commentForm", commentForm);
            return "/question/detail";
        }
        Comment comment = commentService.createComment(answer, commentForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s#comment_%s", answer.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/voteQuestion/{id}")
    public String commentQuestionVote(@PathVariable("id")Integer id,Principal principal){
        Comment comment = this.commentService.getComment(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/question/detail/%s", comment.getQuestionComment().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/voteAnswer/{id}")
    public String commentAnswerVote(@PathVariable("id")Integer id,Principal principal){
        Comment comment = this.commentService.getComment(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/question/detail/%s", comment.getAnswerComment().getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteQuestion/{id}")
    public String deleteQuestion(@PathVariable("id") Integer id,Principal principal){
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        commentService.delete(comment);
        return String.format("redirect:/question/detail/%s",comment.getQuestionComment().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/deleteAnswer/{id}")
    public String deleteAnswer(@PathVariable("id") Integer id,Principal principal){
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        commentService.delete(comment);
        return String.format("redirect:/question/detail/%s",comment.getAnswerComment().getQuestion().getId());
    }

    @GetMapping("/modifyQuestion/{id}")
    public String modifyQuestionComment(@PathVariable("id") Integer id,Principal principal,CommentForm commentForm){
        Comment comment = commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PostMapping("/modifyQuestion/{id}")
    public String modifyQuestionComment(@PathVariable("id") Integer id,@Valid CommentForm commentForm,BindingResult bindingResult,Principal principal){
        if(bindingResult.hasErrors()){
            return "comment_form";
        }

        Comment comment = commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentService.modify(comment,commentForm.getContent());

        return String.format("redirect:/question/detail/%s",comment.getQuestionComment().getId());
    }

    @GetMapping("/modifyAnswer/{id}")
    public String modifyAnswerComment(@PathVariable("id") Integer id,Principal principal,CommentForm commentForm){
        Comment comment = commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PostMapping("/modifyAnswer/{id}")
    public String modifyAnswerComment(@PathVariable("id") Integer id,@Valid CommentForm commentForm,BindingResult bindingResult,Principal principal){
        if(bindingResult.hasErrors()){
            return "comment_form";
        }

        Comment comment = commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentService.modify(comment,commentForm.getContent());

        return String.format("redirect:/question/detail/%s",comment.getAnswerComment().getQuestion().getId());
    }
}
