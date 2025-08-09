package com.saisai.domain.checkpoint.service;

import static com.saisai.domain.common.exception.ExceptionCode.API_CLIENT_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_NETWORK_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_SERVER_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.API_UNKNOWN_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_CHECKPOINT_API_CALL_FAIL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.checkpoint.client.DurunubiCheckpointClient;
import com.saisai.domain.checkpoint.dto.external.CheckpointResponse;
import com.saisai.domain.checkpoint.dto.CheckpointInfo;
import com.saisai.domain.gpx.dto.GpxKeyPoints;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckpointApiService {

    private static final String REQUIRED_CHECKPOINT_TITLE = "필수경유지";
    private final ObjectMapper objectMapper;
    private final DurunubiCheckpointClient durunubiCheckpointClient;

    public List<CheckpointInfo> getCheckpoints(GpxKeyPoints gpxKeyPoints, String courseIdStr) throws CustomException {

        try {
            String body = durunubiCheckpointClient.getCheckpoints(
                gpxKeyPoints.minLat(),
                gpxKeyPoints.minLon(),
                gpxKeyPoints.maxLat(),
                gpxKeyPoints.maxLon(),
                courseIdStr
            );

            if (Objects.isNull(body) || body.isBlank()) {
                log.warn("체크포인트 API 응답이 비어있습니다.");
                throw new CustomException(COURSE_CHECKPOINT_API_CALL_FAIL);
            }

            CheckpointResponse response = objectMapper.readValue(body, CheckpointResponse.class);

            return response.response().stream()
                .filter(item -> REQUIRED_CHECKPOINT_TITLE.equals(item.title()))
                .map(item -> new CheckpointInfo(item.internalId(), item.lat(), item.lon()))
                .toList();

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
            log.error("코스 체크포인트 API 호출 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(API_UNKNOWN_ERROR);
        }
    }
}
