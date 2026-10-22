
package org.example.passjava.question.feign;

import org.example.passjava.question.common.Result;
import org.example.passjava.question.vo.UmsUserSummaryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "passjava-member")
public interface UserFeignClient {

    @GetMapping("/internal/{id}")
    Result<UmsUserSummaryVO> getUser(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );
}
