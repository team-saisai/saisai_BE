package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_INFO_GET_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_LIST_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.constant.CourseType;
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
import org.springframework.data.domain.Sort;
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
        description = "코스명, 요약, 난이도(상(3)/중(2)/하(1)), 거리(km), 예상 소요시간(분), 시군, 참가자수,챌린지 상태, 챌린지 종료일, 이벤트 여부, 지급 리워드 한 페이지 당 10개 씩 반환")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CoursePageRes>>> getAllCourses(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "challenge(챌린지 코스), general(일반 코스)") @RequestParam(defaultValue = "challenge") String type,
        @Parameter(description = "levelAsc(난이도 낮은 순), levelDesc(난이도 높은 순), participantsDesc(참가자 순), endSoon(종료일 순)") @RequestParam(defaultValue = "levelAsc") String sort
    ) {
        CourseType courseType = CourseType.from(type);
        CourseSortOption sortOption = CourseSortOption.from(sort);

        Pageable pageable = PageRequest.of(
            page - 1,
            10,
            Sort.by(sortOption.getDirection(), sortOption.getSortColumn())
        );
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_LIST_GET_SUCCESS, courseService.getCourses(pageable, courseType, sortOption)));
    }

    @Operation(summary = "코스 상세 조회",
        description = "코스ID, 코스명, 코스 설명, 난이도(상(3)/중(2)/하(1)), 거리(km), 예상 소요시간(분), 시군(지역), gpx경로(위도, 경도, 고도, 앞뒤 좌표 거리(m), 현재까지의 누적 거리(km)), 도전자 수, 완주자 수, 라이딩 ID, 챌린지 종료일(Ended(종료), Ongoing(진행중), null), 챌린지 종료일, 이벤트 여부 ")
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDetailsRes>> getCourseInfo(
        @PathVariable Long courseId,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_INFO_GET_SUCCESS, courseService.getCourseInfo(courseId, authUserDetails)));
    }
}