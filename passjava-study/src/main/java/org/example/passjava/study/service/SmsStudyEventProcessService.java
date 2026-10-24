package org.example.passjava.study.service;

import org.example.passjava.study.entity.SmsStudyViewEvent;

public interface SmsStudyEventProcessService {
    void process(SmsStudyViewEvent event);
}
