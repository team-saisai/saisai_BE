package com.saisai.domain.reward.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.REWARD_EVENT_CHALLENGE_CONFLICT;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.reward.dto.request.RewardEventReq;
import com.saisai.domain.reward.entity.RewardEvent;
import com.saisai.domain.reward.repository.RewardEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardEventService {

    private final RewardEventRepository rewardEventRepository;
    private final ChallengeRepository challengeRepository;

    // 리워드 이벤트 등록
    @Transactional
    public void createRewardEvent(RewardEventReq rewardEventReq) {
        List<Challenge> challenges = validateAndGetChallenge(rewardEventReq.challengeIds());
        validateEventConflict(rewardEventReq.challengeIds(),
                            rewardEventReq.startTime(),
                            rewardEventReq.endTime());

        List<RewardEvent> rewardEvents = challenges.stream()
            .map(challenge -> RewardEvent.from(rewardEventReq, challenge))
            .toList();

        rewardEventRepository.saveAll(rewardEvents);
    }

    // 챌린지 존재 검사
    private List<Challenge> validateAndGetChallenge(List<Long> challengeIds) {
        List<Challenge> challenges = challengeRepository.findAllById(challengeIds);

        if (challenges.size() != challengeIds.size()) {
            Set<Long> foundIds = challenges.stream()
                .map(Challenge::getId)
                .collect(Collectors.toSet());

            String missingIds = challengeIds.stream()
                .filter(id -> !foundIds.contains(id))
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

            throw new CustomException(COURSE_NOT_FOUND,
                "존재하지 않는 코스 ID: " + missingIds);
        }

        return challenges;
    }

    // 이벤트 진행 중인 챌린지인지 확인
    private void validateEventConflict(List<Long> challengeIds, LocalDateTime startTime, LocalDateTime endTime) {
        List<Long> conflictChallengeeIds = rewardEventRepository.findConflictingChallengeIds(challengeIds,
            startTime, endTime);

        if (!conflictChallengeeIds.isEmpty()) {
            throw new CustomException(REWARD_EVENT_CHALLENGE_CONFLICT,
                "이미 다른 이벤트로 등룩 중인 챌린지: " + conflictChallengeeIds);
        }
    }
}
