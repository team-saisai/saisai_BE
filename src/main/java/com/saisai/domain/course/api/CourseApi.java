package com.saisai.domain.course.api;

import static com.saisai.domain.common.exception.ExceptionCode.API_CLIENT_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_NETWORK_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_SERVER_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_UNKNOWN_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_API_CALL_FAIL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.api.dto.Items;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.FirstGpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseApi {

    @Value("${durunubi.secret}")
    private String API_SECERET_KEY;

    private static final TypeReference<ExternalResponse<Body<CourseItem>>> COURSE_API_RESPONSE_TYPE =
        new TypeReference<ExternalResponse<Body<CourseItem>>>() {};

    private static final String DEFAULT_MOBILE_OS = "ETC";
    private static final String DEFAULT_MOBILE_APP = "saisai";
    private static final String DEFAULT_BRD_DIV = "DNBW";
    private static final String DEFAULT_RESPONSE_TYPE = "json";
    private static final int GET_COURSE_DEFAULT_NUM_OF_ROWS = 100;// 단건 조회용

    private final ObjectMapper objectMapper;
    private final CourseApiInterface courseApiInterface;
    private final CourseRepository courseRepository;
    private final GpxParser gpxParser;

    // 정보 동기화를 위해 주기적으로 두루누비api 호출하는 스케줄러 메서드
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 작동
    private void scheduledCallCourseApi() {
        syncAllCoursesToDb();
    }

    // 두루누비 API 데이터 DB에 저장하는 메서드
    private void syncAllCoursesToDb() throws CustomException {
        int page = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            ExternalResponse<Body<CourseItem>> result = callCourseApi(page);

            List<CourseItem> currentItems = Optional.ofNullable(result)
                .map(ExternalResponse::response)
                .map(ExternalResponse.Response::body)
                .map(Body::items)
                .map(Items::item)
                .orElseGet(ArrayList::new);

            for (CourseItem item : currentItems) {
                FirstGpxPoint firstGpxPoint = gpxParser.parseFirstGpxpath(item.gpxpath());
                Course course = Course.from(item, firstGpxPoint);
                courseRepository.save(course);
            }

            if (currentItems.size() < GET_COURSE_DEFAULT_NUM_OF_ROWS) {
                hasMoreData = false;
            } else {
                page++;
            }
        }
    }

    // 두루누비(코스)API 호출 메서드
    private ExternalResponse<Body<CourseItem>> callCourseApi(int page) throws CustomException {
        try {
            String result = courseApiInterface.callCourseApi(
                DEFAULT_MOBILE_OS,
                DEFAULT_MOBILE_APP,
                API_SECERET_KEY,
                DEFAULT_BRD_DIV,
                page,
                GET_COURSE_DEFAULT_NUM_OF_ROWS,
                DEFAULT_RESPONSE_TYPE
            );

            if (result == null || result.trim().isEmpty()) {
                log.warn("코스 API 응답이 비어있습니다.");
                throw new CustomException(COURSE_API_CALL_FAIL);
            }

            return objectMapper.readValue(result, COURSE_API_RESPONSE_TYPE);
        } catch (HttpClientErrorException e) {
            log.error("HTTP 클라이언트 오류 발생: 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new CustomException(API_CLIENT_ERROR);
        } catch (HttpServerErrorException e) {
            log.error("HTTP 서버 오류 발생: 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new CustomException(API_SERVER_ERROR);
        } catch (ResourceAccessException e) {
            log.error("네트워크/리소스 접근 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(API_NETWORK_ERROR);
        } catch (Exception e) {
            log.error("API 호출 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(API_UNKNOWN_ERROR);
        }
    }
}
