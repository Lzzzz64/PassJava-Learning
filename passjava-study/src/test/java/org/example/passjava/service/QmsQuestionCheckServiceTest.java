package org.example.passjava.service;

import feign.FeignException;
import feign.Request;
import org.example.passjava.study.VO.QmsQuestionVO;
import org.example.passjava.study.common.Result;
import org.example.passjava.study.feign.QuestionFeignClient;
import org.example.passjava.study.service.impl.QmsQuestionCheckServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@ExtendWith(MockitoExtension.class)
public class QmsQuestionCheckServiceTest {
    @Mock
    private QuestionFeignClient questionFeignClient;

    @InjectMocks
    private QmsQuestionCheckServiceImpl qmsQuestionCheckServiceImpl;

    private Request request() {
        return Request.create(
                Request.HttpMethod.GET,
                "/questions/3",
                Map.of(),
                null,
                null,
                null
        );
    }

    @Test
    public void checkExists() {
        when(questionFeignClient.getQuestionById(1L))
            .thenReturn(Result.success(new QmsQuestionVO(1L, "What is your name?")));

        assertDoesNotThrow(() -> qmsQuestionCheckServiceImpl.checkExists(1L));
        verify(questionFeignClient, times(1)).getQuestionById(1L);
    }

    @Test
    public void checkNotExists() {
        when(questionFeignClient.getQuestionById(999L))
                .thenThrow(new FeignException.NotFound(
                        "Not Found",
                        request(),
                        null,
                        null
                ));
        assertThrows(ResponseStatusException.class, () -> qmsQuestionCheckServiceImpl.checkExists(999L));
        verify(questionFeignClient, times(1)).getQuestionById(999L);
    }

    @Test
    public void checkNoServiceError() {
        when(questionFeignClient.getQuestionById(1L))
                .thenThrow(new FeignException.InternalServerError(
                        "Internal Server Error",
                        request(),
                        null,
                        null
                ));
        assertThrows(ResponseStatusException.class, () -> qmsQuestionCheckServiceImpl.checkExists(1L));
        verify(questionFeignClient, times(1)).getQuestionById(1L);
    }
}
