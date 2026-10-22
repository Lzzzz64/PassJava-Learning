package org.example.passjava.question.service;

import org.example.passjava.question.vo.QmsAdminQuestionVO;
import org.springframework.security.oauth2.jwt.Jwt;

public interface QMSQuestionByAdminService {
    QmsAdminQuestionVO getAdminQuestionById(Long id, Jwt jwt);
}
