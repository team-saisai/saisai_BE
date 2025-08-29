package com.saisai.domain.reward.event;

import com.saisai.domain.reward.service.UserRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserRewardEventListener {

    private final UserRewardService userRewardService;

    @TransactionalEventListener
    @Async("userRewardExecutor")
    public void handleUserRewardEvent(UserRewardEvent event) {
        userRewardService.earnReward(event.userId(), event.rewardInfo());
    }

}
