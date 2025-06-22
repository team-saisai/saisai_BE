package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_API_CALL_FAIL;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NAME_REQUIRED;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import com.saisai.domain.common.utils.ExternalResponseUtil;
import com.saisai.domain.course.api.CourseApi;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.dto.response.CourseInfoRes;
import com.saisai.domain.course.dto.response.CourseListItemRes;
import com.saisai.domain.course.dto.response.CourseSummaryInfo;
import com.saisai.domain.course.entity.CourseImage;
import com.saisai.domain.course.repository.CourseImageRepository;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.ride.repository.RideRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final CourseImageRepository courseImageRepository;
    private final CourseApi courseApi;

    // 코스 목록 조회 비즈니스 로직
    public Page<CourseListItemRes> getCourses(int page) {
        ExternalResponse<Body<CourseItem>> apiResponse = callCourseApiWithExceptionHandling(
            () -> courseApi.callCourseApi(page),"목록");

        List<CourseItem> items = ExternalResponseUtil.extractItems(apiResponse);

        Long totalCount = ExternalResponseUtil.extractTotalCount(apiResponse);
        int currentPage = ExternalResponseUtil.extractPageNo(apiResponse, page).intValue();
        int pageSizeFromApi = ExternalResponseUtil.extractNumOfRows(apiResponse).intValue();
        int actualPageSize = getValidatedPageSize(pageSizeFromApi);

        validatePageRequest(totalCount, currentPage, actualPageSize);

        List<CourseListItemRes> courseItemResList = convertCourseItems(items);

        return new PageImpl<>(courseItemResList, PageRequest.of(currentPage, actualPageSize), totalCount);
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

        Long completeUserCount = rideRepository.countByCourseNameAndStatus(courseName);

        Challenge challenge = challengeRepository.findByCourseNameAndStatus(courseName,
            ChallengeStatus.ONGOING);

        return CourseInfoRes.from(courseItem, completeUserCount, challenge);
    }

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

    private ExternalResponse<Body<CourseItem>> callCourseApiWithExceptionHandling(
        Supplier<ExternalResponse<Body<CourseItem>>> apiCall, String errorMessagePrefix) throws CustomException
    {
        try {
            return apiCall.get();
        } catch (CustomException e) {
            log.error("코스 {} 조회 중 API 호출 또는 JSON 파싱 중 예외 발생: {}", errorMessagePrefix, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("코스 {} 조회 중 예상치 못한 예외 발생: {}", errorMessagePrefix, e.getMessage(), e);
            throw new CustomException(COURSE_API_CALL_FAIL);
        }
    }

    private List<CourseListItemRes> convertCourseItems(List<CourseItem> items) {
        List<CourseListItemRes> courseItemResList = new ArrayList<>();
        HashSet<String> courseNameSet = new HashSet<>();

        for (CourseItem item : items) {

            if (item == null || item.courseName() == null || item.courseName().trim().isEmpty()) {
                log.warn("유효하지 않은 CourseItem이 감지되었습니다 (null 객체 또는 courseName이 null/비어있음). 스킵합니다: {}", item);
                continue;
            }

            Challenge challenge = challengeRepository.findByCourseName(item.courseName());
            CourseImage courseImage = courseImageRepository.findCourseImageByCourseName(item.courseName());

            try {
                CourseListItemRes convertedItem = CourseListItemRes.from(item, challenge, courseImage);
                if (convertedItem != null) {
                    String currentCourseName = convertedItem.courseName();
                    if (currentCourseName != null && courseNameSet.add(currentCourseName)) {
                        courseItemResList.add(convertedItem);
                    } else {
                        log.warn("중복 또는 null로 인해 코스 아이템 추가 스킵: {}", currentCourseName);
                    }
                }
            } catch (CustomException e) {
                log.warn("코스 아이템 변환 중 비즈니스 예외 발생 (Course ID: {}): {}. 스택 트레이스: {}",
                    item.courseName(), e.getMessage(), e.toString(), e);
            } catch (Exception e) {
                log.error("코스 아이템 변환 중 예상치 못한 시스템 오류 발생 (Course ID: {}): {}. 스택 트레이스: {}",
                    item.courseName(), e.getMessage(), e.toString(), e);
            }
        }

        return courseItemResList;
    }

    private void validatePageRequest(Long totalCount, int page, int pageSize) {

        if (totalCount > 0) {
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            if (page > totalPages) {
                log.warn("요청 페이지 번호가 유효 범위를 초과했습니다. 요청 페이지: {}, 총 페이지: {}", page, totalPages);
                throw new CustomException(ExceptionCode.INVALID_PAGE_NUMBER);
            }
        }
    }

    private int getValidatedPageSize(int pageSize) {
        if (pageSize < 1) {
            log.warn("외부 API에서 유효하지 않은 페이지 크기({})를 반환했습니다. 기본값 10으로 대체합니다.",
                pageSize);
            return 10;
        }
        return pageSize;
    }
}
