package org.example.passjava.question.controller;

import jakarta.validation.Valid;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.service.QmsQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")

public class QMSQuestionController {
    @Autowired
    private QmsQuestionService qmsQuestionService;

    @GetMapping
    public List<QmsQuestion> list() {
        return qmsQuestionService.list();
    }

    @GetMapping("/{id}")
    public QmsQuestion getById(@PathVariable Long id) {
        return qmsQuestionService.getById(id);
    }

    @PostMapping
    public Boolean create(@RequestBody QmsQuestionDTO qmsQuestionDTO) {
        return qmsQuestionService.saveQuestion(qmsQuestionDTO);
    }

    @PutMapping("/{id}")
    public Boolean update(@PathVariable Long id, @RequestBody QmsQuestionDTO qmsQuestionDTO) {
        return qmsQuestionService.updateQuestion(id, qmsQuestionDTO);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        return qmsQuestionService.removeById(id);
    }
}
