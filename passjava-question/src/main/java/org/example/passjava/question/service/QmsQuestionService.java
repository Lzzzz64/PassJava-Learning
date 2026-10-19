package org.example.passjava.question.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.passjava.question.dto.QmsQuestionQueryDTO;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.entity.QmsQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.passjava.question.vo.PageVO;
import org.example.passjava.question.vo.QmsQuestionVO;

/**
* @author 123456
* @description 针对表【qms_question】的数据库操作Service
* @createDate 2026-10-19 14:55:51
*/
public interface QmsQuestionService extends IService<QmsQuestion> {
    Boolean saveQuestion(QmsQuestionDTO qmsQuestionDTO);
    Boolean updateQuestion(Long id, QmsQuestionDTO qmsQuestionDTO);
    PageVO<QmsQuestionVO> page(QmsQuestionQueryDTO query);
}
