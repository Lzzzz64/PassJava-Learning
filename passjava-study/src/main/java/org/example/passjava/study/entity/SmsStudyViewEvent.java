package org.example.passjava.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class SmsStudyViewEvent {
    private String eventId;
    private Long userId;
    private Long questionId;
    private Date occurredAt;
}
