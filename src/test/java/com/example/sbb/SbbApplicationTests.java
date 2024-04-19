package com.example.sbb;


import com.example.sbb.answer.AnswerService;
import com.example.sbb.category.Category;
import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionRepository;
import com.example.sbb.question.QuestionService;
import com.example.sbb.user.SiteUser;
import com.example.sbb.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

@SpringBootTest
class SbbApplicationTests {
	@Autowired
	private QuestionService questionService;


//	@Test
//	void testJpa() {
//		for (int i = 1; i <= 300; i++) {
//			String subject = String.format("테스트 데이터입니다:[%03d]", i);
//			String content = "내용무";
//			this.questionService.create(subject, content,null, Category category);
//		}
//	}

	@Autowired
	UserRepository userRepository;

	@Test
	@Transactional
	@Rollback(value = false)
	void t1(){
		SiteUser siteUser = new SiteUser();
		siteUser.setUsername("hong");
		siteUser.setPassword("123");
		siteUser.setEmail("hong@naver.com");
		userRepository.save(siteUser);
	}
}
