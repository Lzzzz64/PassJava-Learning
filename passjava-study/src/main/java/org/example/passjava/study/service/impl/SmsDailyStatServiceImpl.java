package org.example.passjava.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.study.VO.SmsDailyStatVO;
import org.example.passjava.study.entity.SmsDailyStat;
import org.example.passjava.study.service.SmsDailyStatService;
import org.example.passjava.study.mapper.SmsDailyStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
* @author 123456
* @description 针对表【sms_daily_stat】的数据库操作Service实现
* @createDate 2026-10-24 11:59:52
*/
@Service
public class SmsDailyStatServiceImpl extends ServiceImpl<SmsDailyStatMapper, SmsDailyStat>
    implements SmsDailyStatService{
    @Autowired
    private SmsDailyStatMapper smsDailyStatMapper;

    @Override
    public List<SmsDailyStatVO> last7Days(Long userId) {
        LocalDate end = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        LocalDate start = end.minusDays(6);

        List<SmsDailyStatVO> records =
                smsDailyStatMapper.selectRange(userId, start, end);

        Map<LocalDate, Integer> statMap = new HashMap<>();
        for (SmsDailyStatVO record : records) {
            statMap.put(record.getDate(), record.getViewCount());
        }

        List<SmsDailyStatVO> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);

            SmsDailyStatVO vo = new SmsDailyStatVO();
            vo.setDate(date);
            vo.setViewCount(statMap.getOrDefault(date, 0));

            result.add(vo);
        }

        return result;
    }
}




