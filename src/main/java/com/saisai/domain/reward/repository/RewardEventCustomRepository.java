package com.saisai.domain.reward.repository;

import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDate;
import java.util.List;

public interface RewardEventCustomRepository {

    List<RewardEvent> findRewardEventScheduled(LocalDate date);

    List<RewardEvent> findRewardEventActived(LocalDate date);

}
