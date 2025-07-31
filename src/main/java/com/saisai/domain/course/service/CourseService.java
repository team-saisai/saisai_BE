package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION_FOR_COURSE_TYPE;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.aws.s3.GpxS3;
import com.saisai.domain.common.aws.s3.ImageUtil;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.constant.CourseType;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.response.CourseDetailsRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final GpxParser gpxParser;
    private final ImageUtil imageUtil;
    private final GpxS3 gpxS3;
    private final UserRepository userRepository;

    // 코스 목록 조회 메서드
    public Page<CoursePageRes> getCourses(Pageable pageable, CourseType type, CourseSortOption sortOption, AuthUserDetails authUserDetails) {

        return switch (type) {
            case CHALLENGE -> fetchChallengeCoursesAsPage(pageable, sortOption, authUserDetails.userId());
            case GENERAL -> fetchGeneralCoursesAsPage(pageable, sortOption, authUserDetails.userId());
        };
    }

    // 챌린지 코스 조회
    private Page<CoursePageRes> fetchGeneralCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {

        if (sortOption.equals(CourseSortOption.END_SOON)) {
            throw new CustomException(INVALID_SORT_OPTION_FOR_COURSE_TYPE);
        }

        Page<GeneralCourseProjection> generalPage = courseRepository.findGeneralCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = generalPage.getContent().stream()
            .map(projection ->
                CoursePageRes.from(
                    projection,
                    imageUtil.getImageUrl(projection.imageUrl())
                ))
            .toList();
        return new PageImpl<>(result, pageable, generalPage.getTotalElements());
    }

    // 일반 코스 조회
    private Page<CoursePageRes> fetchChallengeCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {
        Page<ChallengeCourseProjection> challengePage = courseRepository.findChallengeCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = challengePage.getContent().stream()
            .map(projection ->
                CoursePageRes.from(
                    projection,
                    imageUtil.getImageUrl(projection.imageUrl())
                ))
            .toList();
        return new PageImpl<>(result, pageable, challengePage.getTotalElements());
    }

    // 코스 상세 조회 비즈니스 로직
    public CourseDetailsRes getCourseInfo(Long courseId, AuthUserDetails authUserDetails) {

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        CourseDetailsProjection course = courseRepository.findCourseDetailsProjection(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        Long rideId = rideRepository.findActiveRideIdByUserIdAndCourseId(user.getId(), courseId);

        RideCountRes rideCountRes = rideRepository.countRideByCourseId(courseId);

        String gpxContent = gpxS3.getGpxContent(course.gpxpath());
        List<GpxPoint> gpxPoints = gpxParser.parseGpxContent(gpxContent);

        return CourseDetailsRes.from(course, imageUtil.getImageUrl(course.imageUrl()), rideCountRes, gpxPoints, rideId);
    }
}
