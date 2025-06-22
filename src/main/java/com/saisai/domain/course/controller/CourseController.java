package com.saisai.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 전체 목록 조회",
        description = "코스명, 요약, 난이도, 거리(km), 예상 소요시간(분), 시군, 도전자 수, 완주자 수, 챌린지 상태, 챌린지 종료일을 한 페이지 당 10개 씩 반환\n\n" +
            "페이지 번호 파라미터만 활성화 상태")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseListItemRes>>> getAllCourses(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page
        //@Parameter(description = "난이도") @RequestParam(required = false) Integer level,
        //@Parameter(description = "거리") @RequestParam(required = false) Double distance,
        //@Parameter(description = "시군 (지역)") @RequestParam(required = false) String sigun
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_LIST_GET_SUCCESS, courseService.getCourses(page)));
    }
