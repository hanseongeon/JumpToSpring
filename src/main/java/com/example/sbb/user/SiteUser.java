package com.example.sbb.user;

import com.example.sbb.answer.Answer;
import com.example.sbb.comment.Comment;
import com.example.sbb.question.Question;
import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

    private String profile_image;

    private String Role;

    private String provider;
    private String providerId;

    @Builder
    public SiteUser(String username, String password, String email, String role, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.Role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}