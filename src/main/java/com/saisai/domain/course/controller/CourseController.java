package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_LIST_GET_SUCCESS;

import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.response.GetCourseInfoRes;
import com.saisai.domain.course.dto.response.GetCourseListRes;
import com.saisai.domain.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name= "코스 관련 API")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 전체 목록 조회",
        description = "코스ID, 코스명, 요약, 난이도, 거리(km), 예상 소요시간(분), 시군 등을 한 페이지 당 10개 씩 반환 ")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetCourseListRes>>> getAllCourses(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "난이도") @RequestParam(required = false) Integer level,
        @Parameter(description = "거리") @RequestParam(required = false) Double distance,
        @Parameter(description = "시군 (지역)") @RequestParam(required = false) String sigun
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_LIST_GET_SUCCESS, courseService.getCourses(page)));
    }
}
