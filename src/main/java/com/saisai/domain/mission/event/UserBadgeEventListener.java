package com.saisai.domain.mission.event;

import com.saisai.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserBadgeEventListener {

    private final MissionService missionService;

    @TransactionalEventListener
    @Async("userRewardExecutor")
    public void handleUserBadgeEvent(UserBadgeEvent event) {
        missionService.checkAndGrantAllMissions(event.user());
    }
}
