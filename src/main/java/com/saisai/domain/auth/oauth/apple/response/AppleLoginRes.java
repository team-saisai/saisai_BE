package com.saisai.domain.auth.oauth.apple.response;

import com.saisai.domain.auth.oauth.UserInfo;

public record AppleLoginRes(
    String refreshToken,
    UserInfo userInfo
) {

}
