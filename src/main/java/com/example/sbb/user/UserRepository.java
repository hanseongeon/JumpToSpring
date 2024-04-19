package com.example.sbb.user;

import com.example.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByusername(String username);

    Optional<SiteUser> findByEmail(String email);

    Page<Question> findByQuestionList(SiteUser siteUser, Pageable pageable);


}