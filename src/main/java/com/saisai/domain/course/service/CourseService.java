package com.saisai.domain.course.service;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.course.dto.response.GetCourseInfoRes;
import com.saisai.domain.course.dto.response.GetCourseListRes;
import com.saisai.domain.course.entity.CourseApi;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final RideRepository rideRepository;
    private final ChallengeRepository challengeRepository;
    private final CourseApi courseApi;

    public Page<GetCourseListRes> getCourses(int page) throws Exception {
        JSONObject courseBody = courseApi.callCourseApiBody(page-1);
        return GetCourseListRes.createCourseList(courseBody);
    }

    public GetCourseInfoRes getCourseInfo(String courseName) {
        JSONArray courseItem = courseApi.callCourseApiItems(courseName);

        Long completeUserCount = rideRepository.countByCourseNameAndStatus(courseName);

        Challenge challenge = challengeRepository.findByCourseNameAndStatus(courseName,
            ChallengeStatus.ONGOING);

        return GetCourseInfoRes.createCourseInfo(courseItem, completeUserCount, challenge);
    }
}
