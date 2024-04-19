package com.example.sbb.answer;

import com.example.sbb.question.Question;
import com.example.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Page<Answer> findAll(Pageable pageable);

    Page<Answer> findByQuestionId(Pageable pageable, int questionId);


//    Page<Answer> findAllByQuestion(Pageable pageable, Question question);

    @Query(value="select "
            + "distinct a.*, count(av.answer_id) as voter_count "
            + "from answer a "
            + "left outer join answer_voter av on av.answer_id=a.id "
            + "where a.question_id = :questionId "
            + "group by a.id, av.answer_id "
            + "order by voter_count desc, a.create_date desc "
            , countQuery = "select" +
            "             a.*, count(av.answer_id) as voter_count" +
            "             from answer a" +
            "             left outer join answer_voter av on av.answer_id=a.id" +
            "             where a.question_id = :questionId" +
            "             group by a.id, av.answer_id"
            , nativeQuery = true)
    Page<Answer> findAllByQuestion(@Param("questionId") Integer questionId, Pageable pageable);

    Page<Answer> findByAuthor(SiteUser siteUser,Pageable pageable);

}
