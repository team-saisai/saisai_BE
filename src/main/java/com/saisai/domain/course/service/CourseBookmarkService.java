package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_ALREADY_BOOKMARK;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_BOOKMARK_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION_FOR_COURSE_TYPE;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.aws.s3.ImageUtil;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.constant.CourseType;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.request.BookmarksRemoveReq;
import com.saisai.domain.course.dto.response.CourseBookmarkRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.entity.CourseBookmark;
import com.saisai.domain.course.repository.CourseBookmarkRepository;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseBookmarkService {

    private final CourseRepository courseRepository;
    private final CourseBookmarkRepository courseBookMarkRepository;
    private final UserRepository userRepository;
    private final ImageUtil imageUtil;

    // 코스 북마크 추가
    @Transactional
    public CourseBookmarkRes bookmarkCourse(Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (isBookmarkExists(course, user)) {
            throw new CustomException(COURSE_ALREADY_BOOKMARK);
        }

        CourseBookmark courseBookMark = CourseBookmark.from(user, course);

        CourseBookmark savedCourseBookmark = courseBookMarkRepository.save(courseBookMark);

        return CourseBookmarkRes.of(true);
    }

    // 코스 북마크 삭제
    @Transactional
    public CourseBookmarkRes removeBookmark (Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        CourseBookmark courseBookmark = courseBookMarkRepository.findByCourseIdAndUserId(course.getId(), user.getId())
            .orElseThrow(() -> new CustomException(COURSE_BOOKMARK_NOT_FOUND));

        courseBookMarkRepository.delete(courseBookmark);

        return CourseBookmarkRes.of(false);

    }

    // 북마크 여러개 삭제
    @Transactional
    public void removeBookmarks(BookmarksRemoveReq bookmarksRemoveReq, AuthUserDetails authUserDetails) {

        int deletedCount = courseBookMarkRepository.deleteByUserIdAndCourseIdIn(
            authUserDetails.userId(), bookmarksRemoveReq.courseIds()
        );

        if (deletedCount == 0) {
            throw new CustomException(COURSE_BOOKMARK_NOT_FOUND);
        }
    }

    // 저장한 코스 조회
    public Page<CoursePageRes> getBookmarkCourses(Pageable pageable, CourseType type, CourseSortOption sortOption, AuthUserDetails authUserDetails) {
        return switch (type) {
            case CHALLENGE -> fetchChallengeCoursesAsPage(pageable, sortOption, authUserDetails.userId());
            case GENERAL -> fetchGeneralCoursesAsPage(pageable, sortOption, authUserDetails.userId());
        };
    }

    // 저장한 챌린지 코스 조회
    private Page<CoursePageRes> fetchChallengeCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {
        Page<ChallengeCourseProjection> challengePage = courseBookMarkRepository.findChallengeBookmarkCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = challengePage.getContent().stream()
            .map(projection ->
                CoursePageRes.from(
                    projection,
                    imageUtil.getImageUrl(projection.imageUrl())
                ))
            .toList();
        return new PageImpl<>(result, pageable, challengePage.getTotalElements());
    }

    // 저장한 일반 코스 조회
    private Page<CoursePageRes> fetchGeneralCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {

        if (sortOption.equals(CourseSortOption.END_SOON)) {
            throw new CustomException(INVALID_SORT_OPTION_FOR_COURSE_TYPE);
        }

        Page<GeneralCourseProjection> generalPage = courseBookMarkRepository.findGeneralBookmarkCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = generalPage.getContent().stream()
            .map(projection ->
                CoursePageRes.from(
                    projection,
                    imageUtil.getImageUrl(projection.imageUrl())
                ))
            .toList();
        return new PageImpl<>(result, pageable, generalPage.getTotalElements());
    }


    // 코스 저장 이미 존재하는지 확인
    private boolean isBookmarkExists(Course course, User user) {
        return courseBookMarkRepository.existsByCourseIdAndUserId(course.getId(), user.getId());
    }
}
