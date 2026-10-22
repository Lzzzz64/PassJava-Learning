package org.example.passjava.question.service.impl;

import org.example.passjava.question.common.Result;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.entity.QmsType;
import org.example.passjava.question.feign.UserFeignClient;
import org.example.passjava.question.mapper.QmsQuestionMapper;
import org.example.passjava.question.mapper.QmsTypeMapper;
import org.example.passjava.question.service.QMSQuestionByAdminService;
import org.example.passjava.question.vo.QmsAdminQuestionVO;
import org.example.passjava.question.vo.UmsUserSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QMSQuestionByAdminServiceImpl implements QMSQuestionByAdminService {
    @Autowired
    private QmsQuestionMapper qmsQuestionMapper;
    @Autowired
    private QmsTypeMapper qmsTypeMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Override
    public QmsAdminQuestionVO getAdminQuestionById(Long id, Jwt jwt) {
        QmsQuestion qmsQuestion = qmsQuestionMapper.selectById(id);
        QmsType type = qmsTypeMapper.selectById(qmsQuestion.getType());

        QmsAdminQuestionVO question = new QmsAdminQuestionVO();
        Result<UmsUserSummaryVO> summaryVOResult = userFeignClient.getUser(qmsQuestion.getCreateBy(), "Bearer " + jwt.getTokenValue());
        UmsUserSummaryVO summaryVO = summaryVOResult.getData();

        question.setId(qmsQuestion.getId());
        question.setTitle(qmsQuestion.getTitle());
        question.setType(qmsQuestion.getType());
        question.setTypeName(type.getType());
        question.setLevel(qmsQuestion.getLevel());
        question.setCreateBy(qmsQuestion.getCreateBy());
        question.setCreateByName(summaryVO.getUsername());

        return question;
    }
}
