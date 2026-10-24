package org.example.passjava.study.VO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SmsDailyStatVO {
    private LocalDate date;
    private Integer viewCount;
}
