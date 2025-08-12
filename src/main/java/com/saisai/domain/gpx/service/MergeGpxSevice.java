package com.saisai.domain.gpx.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.S3_SERVER_UPLOAD_FAIL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saisai.domain.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.client.GpxS3;
import com.saisai.domain.gpx.client.MergeGpxS3;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.dto.format.TrackPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MergeGpxSevice {

    private final MergeGpxJsonParser mergeGpxJsonParser;
    private final GpxParser gpxParser;
    private final CourseRepository courseRepository;
    private final GpxS3 gpxS3;
    private final MergeGpxS3 mergeGpxS3;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<GpxPoint> mergedGpxPoints(Long courseId, List<Checkpoint> checkpoints) {

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        if (course.getMergeGpxPath() != null) {
            String mergeGpxContents = gpxS3.getGpxContent(course.getMergeGpxPath());
            return mergeGpxJsonParser.deserialize(mergeGpxContents);
        } else {
            try {
                String gpxContent = gpxS3.getGpxContent(course.getGpxPath());
                List<TrackPoint> trackPoints = gpxParser.parseGpxContent(gpxContent);

                List<GpxPoint> mergedGpxPoints = gpxParser.mergeGpxAndCheckpoints(trackPoints, checkpoints);

                String mergedJsonS3key = mergeGpxS3.upload(mergedGpxPoints, course.getName());

                course.updateMergeGpxPath(mergedJsonS3key);

                courseRepository.saveAndFlush(course);

                return mergedGpxPoints;
            } catch (JsonProcessingException e) {
                throw new CustomException(S3_SERVER_UPLOAD_FAIL);
            }
        }
    }

}
