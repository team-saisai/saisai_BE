package com.saisai.domain.course.service;

import com.saisai.domain.course.dto.request.CourseCreateReq;
import com.saisai.domain.course.dto.response.CourseCreateRes;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.infra.checkpoint.client.CheckpointS3;
import com.saisai.infra.checkpoint.dto.response.Checkpoint;
import com.saisai.infra.gpx.client.GpxS3;
import com.saisai.infra.gpx.service.GpxParser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCourseService {

    private final GpxS3 gpxS3;
    private final CheckpointS3 checkpointS3;
    private final GpxParser gpxParser;
    private final CourseRepository courseRepository;

    @Transactional
    public CourseCreateRes createCourse(CourseCreateReq request) {
        String gpxContents = gpxParser.convertGpxToString(request.gpxFile());
        String gpxKey = gpxS3.upload(gpxContents, request.name());

        List<Checkpoint> checkpoint = gpxParser.extractRandomCheckpoints(gpxContents, request.checkpointCount());
        String checkPointKey = checkpointS3.uploadCheckpoint(checkpoint, request.name());

        Course course = Course.from(request, gpxKey, checkPointKey, request.checkpointCount());
        courseRepository.save(course);

        return CourseCreateRes.from(course);
    }
}
