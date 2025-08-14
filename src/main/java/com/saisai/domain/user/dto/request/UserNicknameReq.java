package com.saisai.domain.user.dto.request;

import com.saisai.domain.user.annotation.ValidNickname;

public record UserNicknameReq(

    @ValidNickname
    String nickname
) {

}
