package com.example.sbb.comment;

import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    public Page<Comment> findByAuthor(SiteUser siteUser, Pageable pageable);
}
