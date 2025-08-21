package com.saisai.infra.scheduler;

import com.saisai.domain.reward.entity.RewardEvent;
import com.saisai.domain.reward.repository.RewardEventRepository;
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
public class RewardEventScheduler {

    private final RewardEventRepository rewardEventRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void startRewardEvent() {

        log.info("리워드 이벤트 시작 스케줄러를 시작합니다.");

        List<RewardEvent> startRewardEvents = rewardEventRepository.findRewardEventScheduled(LocalDate.now());
        if (startRewardEvents.isEmpty()) {
            log.info("오늘 날짜에 시작하는 리워드 이벤트가 없습니다.");
            return;
        }

        startRewardEvents.forEach(RewardEvent::start);
        rewardEventRepository.saveAll(startRewardEvents);
        log.info("{}개의 리워드 이벤트가 시작 되었습니다. (IDs: {})",
            startRewardEvents.size(),
            startRewardEvents.stream().map(RewardEvent::getId).toList()
        );
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void endRewardEvent() {

        log.info("리워드 이벤트 종료 스케줄러를 시작합니다.");

        List<RewardEvent> endRewardEvents = rewardEventRepository.findRewardEventActived(LocalDate.now().minusDays(1));
        if (endRewardEvents.isEmpty()) {
            log.info("오늘 날짜에 종료하는 리워드 이벤트가 없습니다.");
            return;
        }

        endRewardEvents.forEach(RewardEvent::end);
        rewardEventRepository.saveAll(endRewardEvents);
        log.info("{}개의 리워드 이벤트가 종료 되었습니다. (IDs: {})",
            endRewardEvents.size(),
            endRewardEvents.stream().map(RewardEvent::getId).toList()
        );
    }


}
