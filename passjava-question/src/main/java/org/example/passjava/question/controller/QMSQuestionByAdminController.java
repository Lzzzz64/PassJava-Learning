package org.example.passjava.question.controller;

import org.example.passjava.question.common.Result;
import org.example.passjava.question.service.QMSQuestionByAdminService;
import org.example.passjava.question.vo.QmsAdminQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/admin/questions")
public class QMSQuestionByAdminController {

    @Autowired
    private QMSQuestionByAdminService qmsQuestionByAdminService;

    @GetMapping("/{id}")
    public Result<QmsAdminQuestionVO> getAdminQuestionById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        QmsAdminQuestionVO question = qmsQuestionByAdminService.getAdminQuestionById(id, jwt);
        return Result.success(question, "获取问题成功");
    }
}
