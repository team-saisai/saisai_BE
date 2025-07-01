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
import com.saisai.domain.common.exception.CustomException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private static final int DEFAULT_NUM_OF_ROWS_COURSE_LIST = 10; // 목록 조회용
    private static final int DEFAULT_NUM_OF_ROWS_COURSE_INFO = 1;  // 단건 조회용
    private static final int DEFAULT_PAGE_NO_COURSE_INFO = 1;      // 단건 조회용

    private final ObjectMapper objectMapper;
    private final CourseApiInterface courseApiInterface;

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
