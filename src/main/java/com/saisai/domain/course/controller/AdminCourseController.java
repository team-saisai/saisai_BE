package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_API_SYNC_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_CREATE_SUCCESS;

import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.request.CourseCreateReq;
import com.saisai.domain.course.dto.response.CourseCreateRes;
import com.saisai.domain.course.service.AdminCourseService;
import com.saisai.domain.course.service.CourseSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ADMIN 전용 API")
@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseSyncService courseSyncService;
    private final AdminCourseService adminCourseService;

    @Operation(summary = "두루누비(코스) API 동기화",
        description = "두루누비(코스) API를 호출하여 서버 DB에 저장하도록 하는 API")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncCourseApi() {

        courseSyncService.syncAllCoursesToDb();

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_API_SYNC_SUCCESS));
    }

    @Operation(summary = "코스 생성 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CourseCreateRes>> createCourse(
        @ModelAttribute CourseCreateReq request
    ) {
        CourseCreateRes response = adminCourseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_CREATE_SUCCESS, response));
    }

}
