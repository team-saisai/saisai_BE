package com.saisai.infra.scheduler;

import com.saisai.domain.course.service.CourseSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class CourseSyncScheduler {

    private final CourseSyncService courseSyncService;

    // 정보 동기화를 위해 월 1회 두루누비api 호출하는 스케줄러 메서드
    @Scheduled(cron = "0 0 0 1 * ?")
    public void durunubiCourseSync() {
        log.info("정기 스케줄 - 두루누비 API 데이터 동기화 시작");
        courseSyncService.syncAllCoursesToDb();
        log.info("정기 스케줄 - 두루누비 API 데이터 동기화 완료");
    }
}
