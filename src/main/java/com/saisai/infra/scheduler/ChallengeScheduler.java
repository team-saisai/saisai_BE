package com.saisai.infra.scheduler;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void startChallenge() {

        log.info("챌린지 시작 스케줄러를 시작합니다.");

        List<Challenge> startChallenges = challengeRepository.findChallengesStartingOn(LocalDate.now());
        if (startChallenges.isEmpty()) {
            log.info("오늘 날짜에 시작하는 챌린지가 없습니다.");
            return;
        }

        startChallenges.forEach(Challenge::start);
        challengeRepository.saveAll(startChallenges);
        log.info("{}개의 챌린지가 시작 되었습니다. (IDs: {})",
            startChallenges.size(),
            startChallenges.stream().map(Challenge::getId).toList()
        );

    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void endChallenge() {

        log.info("챌린지 종료 스케줄러를 시작합니다.");

        List<Challenge> endChallenges = challengeRepository.findChallengesEndingOn(LocalDate.now().minusDays(1));
        if (endChallenges.isEmpty()) {
            log.info("어제 날짜에 종료된 챌린지가 없습니다.");
            return;
        }

        endChallenges.forEach(Challenge::end);
        challengeRepository.saveAll(endChallenges);
        log.info("{}개의 챌린지가 종료 되었습니다.",
            endChallenges.size(),
            endChallenges.stream().map(Challenge::getId).toList()
        );
    }

}
