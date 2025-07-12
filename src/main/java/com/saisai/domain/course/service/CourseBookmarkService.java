package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_ALREADY_SAVE;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.dto.response.CourseBookmarkRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.entity.CourseBookmark;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.course.repository.CourseBookmarkRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseBookmarkService {

    private final CourseRepository courseRepository;
    private final CourseBookmarkRepository courseBookMarkRepository;
    private final UserRepository userRepository;

    // 코스 저장
    @Transactional
    public CourseBookmarkRes bookmarkCourse(Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (isCourseAlreadySaved(course, user)) {
            throw new CustomException(COURSE_ALREADY_SAVE);
        }

        CourseBookmark courseBookMark = CourseBookmark.from(user, course);

        CourseBookmark savedCourseBookmark = courseBookMarkRepository.save(courseBookMark);

        return CourseBookmarkRes.of();
    }

    // 코스 저장 이미 존재하는지 확인
    private boolean isCourseAlreadySaved(Course course, User user) {
        return courseBookMarkRepository.existsByCourseIdAndUserId(course.getId(), user.getId());
    }
}
