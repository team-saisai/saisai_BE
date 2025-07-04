package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_INFO_GET_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_LIST_GET_SUCCESS;

import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.response.CourseDetailsRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name= "코스 API")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 전체 목록 조회",
        description = "코스명, 요약, 난이도, 거리(km), 예상 소요시간(분), 시군, 도전자 수, 완주자 수, 챌린지 상태, 챌린지 종료일을 한 페이지 당 10개 씩 반환")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CoursePageRes>>> getAllCourses(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "챌린지 상태 (챌린지 중 = ONGOING). 미입력시 모든 코스 조회") @RequestParam(required = false) String challengeStatus
    ) {

        Pageable pageable = PageRequest.of(page - 1, 10);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_LIST_GET_SUCCESS, courseService.getCourses(pageable, challengeStatus)));
    }

    @Operation(summary = "코스 상세 조회",
        description = "코스ID, 코스명, 코스 설명, 난이도, 거리(km), 예상 소요시간(분), 시군(지역), 투어 정보, 여행자 정보, gpx경로, 완주자 수, 리워드 정보 반환 ")
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDetailsRes>> getCourseInfo(
        @PathVariable Long courseId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_INFO_GET_SUCCESS, courseService.getCourseInfo(courseId)));
    }
}