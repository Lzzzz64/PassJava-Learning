package org.example.passjava.question.service;

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
    QmsQuestion getQuestionById(Long id);
    void saveQuestion(QmsQuestionDTO qmsQuestionDTO);
    void updateQuestion(Long id, QmsQuestionDTO qmsQuestionDTO);
    void deleteQuestion(Long id);
    PageVO<QmsQuestionVO> page(QmsQuestionQueryDTO query);
}
