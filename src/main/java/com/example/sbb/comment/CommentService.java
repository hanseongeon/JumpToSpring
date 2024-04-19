package com.example.sbb.comment;

import com.example.sbb.answer.Answer;
import com.example.sbb.question.DataNotFoundException;
import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(Question question, String content, SiteUser siteUser){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setQuestionComment(question);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
       return this.commentRepository.save(comment);
    }
    public Comment createComment(Answer answer, String content, SiteUser siteUser){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAnswerComment(answer);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
      return this.commentRepository.save(comment);
    }

    public void vote(Comment comment,SiteUser siteUser){
        comment.getVoter().add(siteUser);
        commentRepository.save(comment);
    }

    public Comment getComment(Integer id){
        Optional<Comment> comment = this.commentRepository.findById(id);
        if(comment.isPresent()){
            return comment.get();
        } else{
            throw new DataNotFoundException("comment not found");
        }
    }

    public void delete(Comment comment){
        this.commentRepository.delete(comment);
    }

    public void modify(Comment comment, String content){
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

}
