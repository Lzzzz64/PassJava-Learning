package org.example.passjava.study.service.impl;

import feign.FeignException;
import org.example.passjava.study.VO.QmsQuestionVO;
import org.example.passjava.study.common.Result;
import org.example.passjava.study.feign.QuestionFeignClient;
import org.example.passjava.study.service.QmsQuestionCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QmsQuestionCheckServiceImpl implements QmsQuestionCheckService {
    @Autowired
    private QuestionFeignClient questionFeignClient;

    @Override
    public void checkExists(Long questionId) {
        try{
            Result<QmsQuestionVO> question = questionFeignClient.getQuestionById(questionId);
            if(question == null || question.getData() == null || question.getData().getId() == null){
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "题库服务返回异常"
                );
            }
        }catch (FeignException.NotFound e){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );
        }catch (FeignException e){
            // 输出 Feign 返回的真实状态码
            System.out.println("Feign调用失败，状态码：" + e.status());
            System.out.println("Feign异常信息：" + e.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "题库服务暂时不可用"
            );
        }

    }
}
