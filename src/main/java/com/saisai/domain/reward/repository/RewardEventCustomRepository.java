package com.saisai.domain.reward.repository;

import com.saisai.domain.reward.dto.projection.RewardInfo;
import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RewardEventCustomRepository {

    List<RewardEvent> findRewardEventScheduled(LocalDate date);

    List<RewardEvent> findRewardEventActived(LocalDate date);

    Optional<RewardInfo> findRewardInfoByRideId(Long rideId);

}
