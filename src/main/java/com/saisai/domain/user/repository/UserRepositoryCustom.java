package com.saisai.domain.user.repository;

import com.saisai.domain.user.dto.response.MypageRes;

public interface UserRepositoryCustom {

    MypageRes findUserInfoById(Long userId);
}
