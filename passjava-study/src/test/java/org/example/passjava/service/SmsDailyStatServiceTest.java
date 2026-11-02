package org.example.passjava.service;

import org.example.passjava.study.VO.SmsDailyStatVO;
import org.example.passjava.study.mapper.SmsDailyStatMapper;
import org.example.passjava.study.service.impl.SmsDailyStatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SmsDailyStatServiceTest {
    @Mock
    private SmsDailyStatMapper smsDailyStatMapper;
    @InjectMocks
    private SmsDailyStatServiceImpl smsDailyStatServiceImpl;

    @Test
    void testLast7Days() {
        Long userId = 1L;
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(6);
        LocalDate end = now;

        when(smsDailyStatMapper.selectRange(userId, start, end))
                .thenReturn(List.of(
                        new SmsDailyStatVO(now.minusDays(6), 1),
                        new SmsDailyStatVO(now.minusDays(5), 2)
                ));

        List<SmsDailyStatVO> smsDailyStatVOS = smsDailyStatServiceImpl.last7Days(userId);
        assertEquals(7, smsDailyStatVOS.size());
        assertEquals(1, smsDailyStatVOS.get(0).getViewCount());
        assertEquals(2, smsDailyStatVOS.get(1).getViewCount());
        assertEquals(0, smsDailyStatVOS.get(2).getViewCount());
        assertEquals(0, smsDailyStatVOS.get(3).getViewCount());
        assertEquals(0, smsDailyStatVOS.get(4).getViewCount());
        assertEquals(0, smsDailyStatVOS.get(5).getViewCount());
        assertEquals(0, smsDailyStatVOS.get(6).getViewCount());

        // 验证只查询了一次数据库
        verify(smsDailyStatMapper, times(1))
                .selectRange(1L, start, end);
    }
}
