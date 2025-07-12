package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_ALREADY_SAVE;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.dto.request.CourseSaveReq;
import com.saisai.domain.course.dto.response.CourseSaveRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.entity.CourseSave;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.course.repository.CourseSaveRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseSaveService {

    private final CourseRepository courseRepository;
    private final CourseSaveRepository courseSaveRepository;
    private final UserRepository userRepository;

    // 코스 저장
    @Transactional
    public CourseSaveRes saveCourse(CourseSaveReq courseSaveReq, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseSaveReq.courseId())
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (isCourseAlreadySaved(course, user)) {
            throw new CustomException(COURSE_ALREADY_SAVE);
        }

        CourseSave courseSave = CourseSave.from(user, course);

        CourseSave saveCourseSave = courseSaveRepository.save(courseSave);

        return CourseSaveRes.from(saveCourseSave);
    }

    // 코스 저장 이미 존재하는지 확인
    private boolean isCourseAlreadySaved(Course course, User user) {
        return courseSaveRepository.existsByCourseIdAndUserId(course.getId(), user.getId());
    }
}
