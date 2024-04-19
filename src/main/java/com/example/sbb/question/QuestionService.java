package com.example.sbb.question;

import com.example.sbb.answer.Answer;
import com.example.sbb.category.Category;
import com.example.sbb.category.CategoryRepository;
import com.example.sbb.category.CategoryService;
import com.example.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    @Autowired
    private final QuestionRepository questionRepository;
    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private final CategoryService categoryService;

    int i = 0;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user, Category category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);
        question.setCategory(category);
        questionRepository.save(question);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();

        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Page<Question> getCategoryIdList(int page, String kw, int id) {
        List<Sort.Order> sorts = new ArrayList<>();

        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = categoryIdSearch(kw,id);
        return this.questionRepository.findAll(spec, pageable);
    }

    public void modify(Question question, String subject, String content,String categoryName) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        if (!question.getCategory().getName().equals(categoryName)) {
            Category category = categoryRepository.findByName(categoryName);
            if (category == null) {
                // 새로운 카테고리인 경우에만 생성하여 설정
                category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
            }
            // 수정된 카테고리를 설정
            question.setCategory(category);
        }
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public void htiPlus(Question question) {
        if (question.getHit() == null) {
            question.setHit(i);
        }
        int a = question.getHit();
        a++;
        question.setHit(a);
        questionRepository.save(question);
    }


    private Specification<Question> categoryIdSearch(String kw, int id) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Question, Category> c = q.join("category", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.and(
                        cb.or(
                                cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                                cb.like(q.get("content"), "%" + kw + "%"), // 내용
                                cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
                                cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                                cb.like(u2.get("username"), "%" + kw + "%") // 답변 작성자
                        ),
                        cb.equal(c.get("id"), id)); // 카테고리 ID
            }
        };
    }

}