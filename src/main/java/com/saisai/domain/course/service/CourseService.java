package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;

import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.ExternalResponseUtil;
import com.saisai.domain.common.utils.ImageUtil;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.course.dto.response.CourseDetailsRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.entity.RideStatus;
import com.saisai.domain.ride.repository.RideRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Map<Long, RideCountRes> rideCountResMap = rideRepository.countRidedMapByCourseIds(courseIds);

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
    public CourseInfoRes getCourseInfo(String courseName) {
        if (courseName.trim().isEmpty()) {
            throw new CustomException(COURSE_NAME_REQUIRED);
        }

        ExternalResponse<Body<CourseItem>> apiResponse = callCourseApiWithExceptionHandling(
            () -> courseApi.callCourseApiByCourseName(courseName), "상세");

        List<CourseItem> items = ExternalResponseUtil.extractItems(apiResponse);
        if (items == null || items.isEmpty()) {
            log.warn("코스명 '{}'에 대한 데이터가 없습니다.", courseName);
            throw new CustomException(COURSE_NOT_FOUND);
        }
        CourseItem courseItem = items.get(0);

        CourseImage courseImage = courseImageRepository.findCourseImageByCourseName(courseName);
        String imageUrl = null;
        if (courseImage != null) {
            imageUrl = courseImage.getUrl();
        }

        Long inprogressUserCont = rideRepository.countByCourseNameAndStatus(courseName, RideStatus.IN_PROGRESS);
        Long completeUserCount = rideRepository.countByCourseNameAndStatus(courseName, RideStatus.COMPLETED);

        List<GpxPoint> gpxPoints = gpxParserService.parseGpxpath(courseItem.gpxpath());

        return CourseInfoRes.from(courseItem, imageUrl, inprogressUserCont, completeUserCount, gpxPoints);
    }

    // 코스아이템 가져오는 메서드
    public Optional<CourseItem> findCourseByName(String courseName) {
        ExternalResponse<Body<CourseItem>> apiResponse = callCourseApiWithExceptionHandling(
            () -> courseApi.callCourseApiByCourseName(courseName), "단건"
        );

        List<CourseItem> items = ExternalResponseUtil.extractItems(apiResponse);
        return Optional.ofNullable(items)
            .filter(courseItemList -> !courseItemList.isEmpty())
            .map(courseItemList -> courseItemList.get(0));
    }
}
