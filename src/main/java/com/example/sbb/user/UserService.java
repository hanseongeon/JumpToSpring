package com.example.sbb.user;

import com.example.sbb.answer.Answer;
import com.example.sbb.answer.AnswerRepository;
import com.example.sbb.comment.Comment;
import com.example.sbb.comment.CommentRepository;
import com.example.sbb.question.DataNotFoundException;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionRepository;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final QuestionRepository questionRepository;

    private final AnswerRepository answerRepository;

    private final CommentRepository commentRepository;
    private final ResourceLoader resourceLoader;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findByEmail(String email) {
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findByUserName(String name) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(name);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void temp_save(SiteUser siteUser) {
        this.userRepository.save(siteUser);
    }

    public String generateTempPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public Page<Question> Paging(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return this.userRepository.findByQuestionList(siteUser, pageable);
    }

    public Page<Question> getPagedQuestionsByUser(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return this.questionRepository.findByAuthor(siteUser, pageable);
    }

    public Page<Answer> getPagedAnswerByUser(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return this.answerRepository.findByAuthor(siteUser, pageable);
    }

    public Page<Comment> getPagedCommentByUser(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return this.commentRepository.findByAuthor(siteUser, pageable);
    }

    public String temp_save(MultipartFile file) {
        if (!file.isEmpty())
            try {
                String path = resourceLoader.getResource("classpath:/static").getFile().getPath();
                File fileFolder = new File(path + "/image");
                if (!fileFolder.exists())
                    fileFolder.mkdirs();
                String filePath = "/image/" + UUID.randomUUID().toString() + "." + file.getContentType().split("/")[1];
                file.transferTo(Paths.get(path + filePath));
                return filePath;
            } catch (IOException ignored) {

            }
        return null;
    }

    public void save(SiteUser user, String url) {
        try {
            String path = resourceLoader.getResource("classpath:/static").getFile().getPath();
            if(user.getProfile_image()!=null) {
                File oldFile = new File(path+user.getProfile_image());
                if(oldFile.exists())
                    oldFile.delete();
            }
            user.setProfile_image(url);
            userRepository.save(user);
        } catch (IOException ignored) {

        }
    }
}