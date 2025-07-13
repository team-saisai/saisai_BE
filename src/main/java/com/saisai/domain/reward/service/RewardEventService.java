package com.saisai.domain.reward.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.REWARD_EVENT_COURSE_CONFLICT;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.reward.dto.request.RewardEventReq;
import com.saisai.domain.reward.entity.EventCourse;
import com.saisai.domain.reward.entity.RewardEvent;
import com.saisai.domain.reward.repository.EventCourseRepository;
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
    private final EventCourseRepository eventCourseRepository;
    private final CourseRepository courseRepository;

    // 리워드 이벤트 등록
    @Transactional
    public void createRewardEvent(RewardEventReq rewardEventReq) {
        List<Course> courses = validateAndGetCourses(rewardEventReq.courseIds());
        validateEventConflict(rewardEventReq.courseIds(),
                            rewardEventReq.startTime(),
                            rewardEventReq.endTime());

        RewardEvent rewardEvent = RewardEvent.from(rewardEventReq);

        RewardEvent saveRewardEvent = rewardEventRepository.save(rewardEvent);

        List<EventCourse> eventCourses = courses.stream()
            .map(course -> EventCourse.from(saveRewardEvent, course))
            .toList();

        eventCourseRepository.saveAll(eventCourses);
    }

    // 코스 존재 검사
    private List<Course> validateAndGetCourses(List<Long> courseIds) {
        List<Course> courses = courseRepository.findAllById(courseIds);

        if (courses.size() != courseIds.size()) {
            Set<Long> foundIds = courses.stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

            String missingIds = courseIds.stream()
                .filter(id -> !foundIds.contains(id))
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

            throw new CustomException(COURSE_NOT_FOUND,
                "존재하지 않는 코스 ID: " + missingIds);
        }


        return courses;
    }

    // 이벤트 진행 중인 코스인지 확인
    private void validateEventConflict(List<Long> courseIds, LocalDateTime startTime, LocalDateTime endTime) {
        List<Long> conflictCourseIds = rewardEventRepository.findConflictingCourseIds(courseIds,
            startTime, endTime);

        if (!conflictCourseIds.isEmpty()) {
            throw new CustomException(REWARD_EVENT_COURSE_CONFLICT,
                "이미 다른 이벤트로 등룩 중인 코스: " + conflictCourseIds);
        }
    }

}
