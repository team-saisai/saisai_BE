package com.saisai.domain.course.service;

import static java.lang.Boolean.TRUE;

import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.api.dto.Items;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.aws.s3.GpxS3;
import com.saisai.domain.course.api.CourseApi;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.FirstGpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApiService {

    private static final int GET_COURSE_DEFAULT_NUM_OF_ROWS = 100;// 단건 조회용

    private final CourseRepository courseRepository;
    private final CourseApi courseApi;
    private final GpxParser gpxParser;
    private final GpxS3 gpxS3;

    // 두루누비 API 데이터 DB에 저장하는 메서드
    @Transactional
    public void syncAllCoursesToDb() throws CustomException {
        int page = 1;
        boolean hasMoreData = true;
        int totalProcessed = 0;
        int duplicateCount = 0;
        int failedCount = 0;
        int newCount = 0;

        while (hasMoreData) {
            ExternalResponse<Body<CourseItem>> result = courseApi.callCourseApi(page);

            List<CourseItem> currentItems = Optional.ofNullable(result)
                .map(ExternalResponse::response)
                .map(ExternalResponse.Response::body)
                .map(Body::items)
                .map(Items::item)
                .orElseGet(ArrayList::new);

            for (CourseItem item : currentItems) {
                totalProcessed++;

                if (TRUE.equals(courseRepository.existsByDurunubiCourseId(item.durunubiCourseId()))) {
                    log.debug("코스 ID {}는 이미 존재합니다. 건너뜁니다.", item.durunubiCourseId());
                    duplicateCount++;
                    continue;
                }

                try {
                    String gpxContent = gpxParser.downloadGpxContent(item.gpxpath());

                    FirstGpxPoint firstGpxPoint = gpxParser.parseFirstGpxpath(gpxContent);

                    String s3GpxPath = gpxS3.upload(gpxContent, item.courseName());

                    Course course = Course.from(item, firstGpxPoint, s3GpxPath);

                    courseRepository.save(course);

                    newCount++;
                    log.info("새 코스 저장 완료: ID={}, 이름={}", item.durunubiCourseId(), item.courseName());
                } catch (Exception e) {
                    log.error("코스 저장 실패: 이름={}, 오류={}", item.courseName(), e.getMessage(), e);
                    failedCount++;
                }
            }

            if (currentItems.size() < GET_COURSE_DEFAULT_NUM_OF_ROWS) {
                hasMoreData = false;
            } else {
                page++;
            }
        }
        log.info("동기화 완료 - 전체: {}건, 신규: {}건, 실패: {}건, 중복: {}건",
            totalProcessed, newCount, failedCount, duplicateCount);
    }

}

