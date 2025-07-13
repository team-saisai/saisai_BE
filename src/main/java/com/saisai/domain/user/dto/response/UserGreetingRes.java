package com.saisai.domain.user.dto.response;

import com.saisai.domain.user.entity.User;

public record UserGreetingRes(
    String nickname
) {
    public static UserGreetingRes from (User user) {
        return new UserGreetingRes(user.getNickname());
    }
}
