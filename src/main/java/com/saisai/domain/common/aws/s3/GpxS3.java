package com.saisai.domain.common.aws.s3;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GpxS3 {

    private final S3Service s3Service;
    private static final String GPX_DIRECTORY = "course/gpx";
    private static final String GPX_FILE_EXTENSION = ".gpx";
    private static final String CONTENT_TYPE = "application/gpx+xml";

    // GPX 파일 s3에 업로드
    public String upload(String gpxContent, String courseName) {
        String sanitizedCourseName = sanitizeFilename(courseName);

        String filename = sanitizedCourseName + "_" +
            UUID.randomUUID().toString().substring(0, 8) + GPX_FILE_EXTENSION;

        return s3Service.uploadContent(gpxContent, GPX_DIRECTORY, filename, CONTENT_TYPE);
    }

    // GPX 파일 삭제
    public void delete(String s3Key) {
        s3Service.delete(s3Key);
    }

    // GPX 파일 내용 가져오기
    public String getGpxContent(String s3Key) {
        return s3Service.getFileContent(s3Key);
    }

    // 파일명에 사용할 수 없는 문자 제거
    private String sanitizeFilename(String courseName) {
        if (courseName == null || courseName.isEmpty()) {
            return "course";
        }

        return courseName
            .replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
            .replaceAll("\\s+", "_")
            .substring(0, Math.min(courseName.length(), 50));
    }
}
