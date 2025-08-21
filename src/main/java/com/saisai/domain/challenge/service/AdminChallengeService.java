package com.saisai.domain.challenge.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;

import com.saisai.domain.challenge.dto.request.CreateChallengeReq;
import com.saisai.domain.challenge.dto.response.CreateChallengeRes;
import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminChallengeService {

    private final ChallengeRepository challengeRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public CreateChallengeRes createChallenges(CreateChallengeReq request) {
        List<Course> coursesToCreate = getCreatableCourses(request.courseIds());

        // 모든 코스가 이미 챌린지 진행 중일 경우
        if (coursesToCreate.isEmpty()) {
            return CreateChallengeRes.of(List.of(), request.startedAt(), request.closedAt(), request.courseIds());
        }

        List<Challenge> newChallenges = coursesToCreate.stream()
            .map(course -> Challenge.create(course, request.startedAt(), request.closedAt()))
            .toList();
        challengeRepository.saveAll(newChallenges);

        List<Long> createdIds = coursesToCreate.stream().map(Course::getId).toList();
        Set<Long> existingIds = request.courseIds().stream()
            .filter(id -> !createdIds.contains(id))
            .collect(Collectors.toSet());

        return CreateChallengeRes.of(createdIds, request.startedAt(), request.closedAt(), existingIds);
    }

    private List<Course> getCreatableCourses(Set<Long> requestCourseIds) {
        List<Course> courses = courseRepository.findAllById(requestCourseIds);

        if (courses.size() != requestCourseIds.size()) {
            Set<Long> foundIds = courses.stream().map(Course::getId).collect(Collectors.toSet());
            Set<Long> invalidIds = requestCourseIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new CustomException(COURSE_NOT_FOUND, "존재하지 않는 코스: " + invalidIds);
        }

        List<Challenge> existingChallenges = challengeRepository.findExistingChallengesByCourse(courses);
        Set<Long> coursesWithExistingChallenges = existingChallenges.stream()
            .map(challenge -> challenge.getCourse().getId())
            .collect(Collectors.toSet());

        return courses.stream()
            .filter(course -> !coursesWithExistingChallenges.contains(course.getId()))
            .toList();
    }
}
