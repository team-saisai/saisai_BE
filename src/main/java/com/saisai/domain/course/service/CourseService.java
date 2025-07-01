package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NAME_REQUIRED;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.ExternalResponseUtil;
import com.saisai.domain.course.api.CourseApi;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.dto.response.CourseInfoRes;
import com.saisai.domain.course.dto.response.CourseListItemRes;
import com.saisai.domain.course.dto.response.CourseSummaryInfo;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.service.GpxParserService;
import com.saisai.domain.ride.entity.RideStatus;
import com.saisai.domain.ride.repository.RideRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private static final int CLIENT_DEFAULT_PAGE_SIZE = 10;
    private final RideRepository rideRepository;
    private final ChallengeRepository challengeRepository;
    private final CourseApi courseApi;
    private final GpxParserService gpxParserService;

    // 코스 목록 조회 비즈니스 로직
    public Page<CourseListItemRes> getCourses(int page, String status) {

        List<CourseItem> resultCourseItems;

        long totalCount;
        int pageNum;
        int pageSize;

        if (ChallengeStatus.ONGOING.toString().equals(status)) {
            Pageable clientPageable = PageRequest.of(page - 1, CLIENT_DEFAULT_PAGE_SIZE);
            List<CourseItem> allCourseItems = fetchAllCourseItems();
            resultCourseItems = filterOnGoingChallengeCourseList(allCourseItems);

            totalCount = resultCourseItems.size();
            pageNum = clientPageable.getPageNumber();
            pageSize = clientPageable.getPageSize();

        } else {
            ExternalResponse<Body<CourseItem>> apiResponse = callCourseApiWithExceptionHandling(
                () -> courseApi.callCourseApi(page),"목록");
            resultCourseItems = ExternalResponseUtil.extractItems(apiResponse);

            totalCount = ExternalResponseUtil.extractTotalCount(apiResponse);
            pageNum = ExternalResponseUtil.extractPageNo(apiResponse, page).intValue();
            pageSize = ExternalResponseUtil.extractNumOfRows(apiResponse).intValue();
        }

        validatePageRequest(totalCount, pageNum, pageSize);

        List<CourseListItemRes> courseItemResList = convertCourseItems(resultCourseItems);

        return new PageImpl<>(courseItemResList, PageRequest.of(pageNum, CLIENT_DEFAULT_PAGE_SIZE), totalCount);
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

    // 코스명 기반의 요약 정보 반환 로직
    public CourseSummaryInfo getCourseSummaryInfoByCourseName(String courseName) {
        if (courseName.trim().isEmpty()) {
            throw new CustomException(COURSE_NAME_REQUIRED);
        }

        ExternalResponse<Body<CourseItem>> apiResponse = callCourseApiWithExceptionHandling(
            () -> courseApi.callCourseApiByCourseName(courseName), "상세"
        );

        List<CourseItem> items = ExternalResponseUtil.extractItems(apiResponse);
        if (items == null || items.isEmpty()) {
            log.warn("코스명 '{}'에 대한 데이터가 없습니다.", courseName);
            throw new CustomException(COURSE_NOT_FOUND);
        }
        CourseItem courseItem = items.get(0);

        String imageUrl = null;
        CourseImage courseImage = courseImageRepository.findCourseImageByCourseName(courseName);
        if (courseImage != null) {
            imageUrl = courseImage.getUrl();
        }

        return CourseSummaryInfo.from(courseItem, imageUrl);
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

    // 챌린지 진행 중인 코스 필터링
    private List<CourseItem> filterOnGoingChallengeCourseList(List<CourseItem> allCourseItemList) {
        List<String> ongoingChallengeCourseNameList = challengeRepository.findChallengeByStatus(ChallengeStatus.ONGOING);

        return allCourseItemList.stream()
            .filter(challengeCourse -> ongoingChallengeCourseNameList.contains(challengeCourse.courseName()))
            .toList();
    }
}
