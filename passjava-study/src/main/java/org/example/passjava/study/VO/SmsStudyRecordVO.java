package org.example.passjava.study.VO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SmsStudyRecordVO {
    private Long questionId;
    private Integer count;
    private Date time;
}
