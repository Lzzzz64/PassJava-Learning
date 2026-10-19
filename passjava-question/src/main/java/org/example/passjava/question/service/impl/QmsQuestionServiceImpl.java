package org.example.passjava.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.service.QmsQuestionService;
import org.example.passjava.question.mapper.QmsQuestionMapper;
import org.example.passjava.question.service.QmsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 123456
* @description 针对表【qms_question】的数据库操作Service实现
* @createDate 2026-10-19 14:55:51
*/
@Service
public class QmsQuestionServiceImpl extends ServiceImpl<QmsQuestionMapper, QmsQuestion>
    implements QmsQuestionService{

    @Autowired
    QmsQuestionMapper qmsQuestionMapper;

    @Override
    public Boolean saveQuestion(QmsQuestionDTO qmsQuestionDTO) {
        QmsQuestion qmsQuestion = new QmsQuestion();
        qmsQuestion.setTitle(qmsQuestionDTO.getTitle());
        qmsQuestion.setAnswer(qmsQuestionDTO.getAnswer());
        qmsQuestion.setLevel(qmsQuestionDTO.getLevel());
        qmsQuestion.setType(qmsQuestionDTO.getType());
        qmsQuestion.setEnable(1);
        qmsQuestion.setDelFlag(0);
        qmsQuestion.setCreateTime(new Date());
        qmsQuestion.setUpdateTime(new Date());
        int i = qmsQuestionMapper.insert(qmsQuestion);
        return i > 0;
    }

    @Override
    public Boolean updateQuestion(Long id, QmsQuestionDTO qmsQuestionDTO) {
        QmsQuestion qmsQuestion = qmsQuestionMapper.selectById(id);
        qmsQuestion.setTitle(qmsQuestionDTO.getTitle());
        qmsQuestion.setAnswer(qmsQuestionDTO.getAnswer());
        qmsQuestion.setLevel(qmsQuestionDTO.getLevel());
        qmsQuestion.setType(qmsQuestionDTO.getType());
        qmsQuestion.setUpdateTime(new Date());
        int i = qmsQuestionMapper.updateById(qmsQuestion);

        return i > 0;
    }
}




