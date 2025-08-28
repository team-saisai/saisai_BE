package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.GPX_FILE_READ_FAIL;
import static com.saisai.domain.common.exception.ExceptionCode.JSON_SERIALIZATION_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.checkpoint.client.CheckpointS3;
import com.saisai.domain.checkpoint.dto.CheckpointInfo;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.dto.request.CourseCreateReq;
import com.saisai.domain.course.dto.response.CourseCreateRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.client.GpxS3;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.service.GpxParser;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminCourseService {

    private final GpxS3 gpxS3;
    private final CheckpointS3 checkpointS3;
    private final GpxParser gpxParser;
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CourseCreateRes createCourse(CourseCreateReq request) {
        String gpxContents = gpxParser.convertGpxToString(request.gpxFile());
        List<GpxPoint> gpxPoints = gpxParser.parseCustomGpxFile(gpxContents);
        String gpxPointsJson = convertListToJson(gpxPoints);
        String gpxKey = gpxS3.upload(gpxPointsJson, request.name());

        List<CheckpointInfo> checkpointInfos = gpxParser.extractRandomCheckpoints(gpxContents);
        String checkPointKey = checkpointS3.upload(checkpointInfos, request.name());

        Course course = Course.from(request, gpxKey, checkPointKey, checkpointInfos.size());
        courseRepository.save(course);

        return CourseCreateRes.from(course);
    }

    private String convertListToJson(List<GpxPoint> gpxPoints) {
        try {
            return objectMapper.writeValueAsString(gpxPoints);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_SERIALIZATION_FAILED);
        }
    }

    public String convertGpxToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new CustomException(GPX_FILE_READ_FAIL);
        }
    }
}
