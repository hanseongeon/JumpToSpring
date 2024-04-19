package com.example.sbb;

import com.example.sbb.question.Question;
import com.example.sbb.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private QuestionRepository questionRepository;


    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }
}
