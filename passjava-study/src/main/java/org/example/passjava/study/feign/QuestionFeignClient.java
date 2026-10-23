package org.example.passjava.study.feign;

import org.example.passjava.study.VO.QmsQuestionVO;
import org.example.passjava.study.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passjava-question")
public interface QuestionFeignClient {
    @GetMapping("/questions/{id}")
    Result<QmsQuestionVO> getQuestionById(@PathVariable("id") Long id);
}
