package com.example.sbb.user;

import com.example.sbb.answer.Answer;
import com.example.sbb.comment.Comment;
import com.example.sbb.question.Question;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    List<Question> questionList;

    @OneToMany(mappedBy = "author",cascade = CascadeType.REMOVE)
    List<Answer> answerList;

    @OneToMany(mappedBy = "author",cascade = CascadeType.REMOVE)
    List<Comment> commentList;

}