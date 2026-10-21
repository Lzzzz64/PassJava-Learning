package org.example.passjava.member.service;

import org.example.passjava.member.vo.UmsUserSummaryVO;

public interface InternalUserService {
    UmsUserSummaryVO getUser(Long id);
}
