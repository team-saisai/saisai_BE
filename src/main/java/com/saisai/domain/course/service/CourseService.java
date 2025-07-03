package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.s3.ImageUtil;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.course.dto.response.CourseDetailsRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.repository.RideRepository;
import java.util.List;
import java.util.Map;
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

    // 코스 목록 조회 메서드
    public Page<CoursePageRes> getCourses(Pageable pageable, String challengeStatus) {
        Page<CoursePageProjection> coursePage = courseRepository.findCoursesByChallengeStatus(challengeStatus, pageable);

        List<Long> courseIds = coursePage.getContent().stream()
            .map(CoursePageProjection::courseId)
            .toList();

        Map<Long, RideCountRes> rideCountResMap = rideRepository.countRidesMapByCourseIds(courseIds);

        List<CoursePageRes> result = coursePage.getContent().stream()
            .map(projection -> {
                RideCountRes rideCountRes = rideCountResMap.getOrDefault(
                    projection.courseId(),
                    RideCountRes.empty(projection.courseId())
                );
                return CoursePageRes.from(
                    projection,
                    rideCountRes,
                    imageUtil.getImageUrl(projection.imageUrl())
                );

            })
            .toList();

        return new PageImpl<>(result, pageable, coursePage.getTotalElements());
    }

    // 코스 상세 조회 비즈니스 로직
    public CourseDetailsRes getCourseInfo(Long courseId) {

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        RideCountRes rideCountRes = rideRepository.countRideByCourseId(courseId);

        List<GpxPoint> gpxPoints = gpxParser.parseGpxpath(course.getGpxPath());

        return CourseDetailsRes.from(course, imageUtil.getImageUrl(course.getImage()), rideCountRes, gpxPoints);
    }
}
