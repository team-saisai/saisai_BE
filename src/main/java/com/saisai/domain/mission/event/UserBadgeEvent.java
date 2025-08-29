package com.saisai.domain.mission.event;

import com.saisai.domain.user.entity.User;

public record UserBadgeEvent(User user) {
    public static UserBadgeEvent from (User user) {
        return new UserBadgeEvent(user);
    }

}
