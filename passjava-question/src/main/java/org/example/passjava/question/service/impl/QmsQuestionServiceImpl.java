package org.example.passjava.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.dto.QmsQuestionQueryDTO;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.entity.QmsType;
import org.example.passjava.question.service.QmsQuestionService;
import org.example.passjava.question.mapper.QmsQuestionMapper;
import org.example.passjava.question.service.QmsTypeService;
import org.example.passjava.question.vo.PageVO;
import org.example.passjava.question.vo.QmsQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author 123456
* @description 针对表【qms_question】的数据库操作Service实现
* @createDate 2026-10-19 14:55:51
*/
@Service
public class QmsQuestionServiceImpl extends ServiceImpl<QmsQuestionMapper, QmsQuestion>
    implements QmsQuestionService{

    @Autowired
    private QmsQuestionMapper qmsQuestionMapper;
    @Autowired
    private QmsTypeService qmsTypeMapper;
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
        if(qmsQuestion == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );
        }
        qmsQuestion.setTitle(qmsQuestionDTO.getTitle());
        qmsQuestion.setAnswer(qmsQuestionDTO.getAnswer());
        qmsQuestion.setLevel(qmsQuestionDTO.getLevel());
        qmsQuestion.setType(qmsQuestionDTO.getType());
        qmsQuestion.setUpdateTime(new Date());
        int i = qmsQuestionMapper.updateById(qmsQuestion);

        return i > 0;
    }

    @Override
    public PageVO<QmsQuestionVO> page(QmsQuestionQueryDTO query) {
        IPage<QmsQuestion> page = new Page<>(
                query.getPage(),
                query.getSize()
        );
        LambdaQueryWrapper<QmsQuestion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(query.getKeyword() != null)
            lambdaQueryWrapper.like(QmsQuestion::getTitle, query.getKeyword());
        if(query.getType() != null)
            lambdaQueryWrapper.eq(QmsQuestion::getType, query.getType());
        if(query.getLevel() != null)
            lambdaQueryWrapper.eq(QmsQuestion::getLevel, query.getLevel());

        IPage<QmsQuestion> result = qmsQuestionMapper.selectPage(page, lambdaQueryWrapper);

        List<QmsQuestion> questions = result.getRecords();
        Set<Long> typeIdSet = new HashSet<>();
        for (QmsQuestion question : questions) {
            if (question.getType() != null) {
                typeIdSet.add(question.getType());
            }
        }
        List<Long> typeIds = new ArrayList<>(typeIdSet);


        Map<Long, String> typeNames = new HashMap<>();
        if (!typeIds.isEmpty()) {
            List<QmsType> types = qmsTypeMapper.listByIds(typeIds);
            for (QmsType type : types)
                typeNames.put(type.getId(), type.getType());
        }

        // 将 Entity 转换为 VO
        List<QmsQuestionVO> records = result.getRecords()
                .stream()
                .map(entity -> new QmsQuestionVO(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getType(),
                        typeNames.get(entity.getType()),
                        entity.getLevel()
                ))
                .toList();

        return new PageVO<>(
                result.getCurrent(),
                result.getSize(),
                result.getTotal(),
                result.getPages(),
                records);
    }
}




