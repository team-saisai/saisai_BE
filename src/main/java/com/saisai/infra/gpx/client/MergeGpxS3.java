package com.saisai.infra.gpx.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.infra.gpx.dto.GpxPoint;
import com.saisai.infra.aws.s3.S3Service;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MergeGpxS3 {

    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    private static final String GPXPOINT_DIRECTORY = "course/gpxpoints";
    private static final String GPXPOINT_FILE_EXTENSION = ".json";
    private static final String CONTENT_TYPE = "application/json";

    // 체크포인트 정보 업로드
    public String upload(List<GpxPoint> gpxPoints, String courseName)
        throws JsonProcessingException {

        String jsonContent = objectMapper.writeValueAsString(gpxPoints);
        String sanitizedCourseName = sanitizeFilename(courseName);

        String filename = sanitizedCourseName + "_" +
            UUID.randomUUID().toString().substring(0, 8) + GPXPOINT_FILE_EXTENSION;

        return s3Service.uploadContent(jsonContent, GPXPOINT_DIRECTORY, filename, CONTENT_TYPE);
    }

    // 체크포인트 JSON 파일 가져오기
    public String getGpxPointContent(String s3Key) {
        return s3Service.getFileContent(s3Key);
    }

    // 파일명에 사용할 수 없는 문자 제거
    private String sanitizeFilename(String courseName) {
        if (courseName == null || courseName.isEmpty()) {
            return "course";
        }

        String sanitized = courseName
            .replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
            .replaceAll("\\s+", "_");

        return sanitized.substring(0, Math.min(sanitized.length(), 50));
    }
}
