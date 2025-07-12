package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_SAVE_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.request.CourseSaveReq;
import com.saisai.domain.course.dto.response.CourseSaveRes;
import com.saisai.domain.course.service.CourseSaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코스 저장 API")
@RestController
@RequestMapping("/api/saved-courses")
@RequiredArgsConstructor
public class CourseSaveController {

    private final CourseSaveService courseSaveService;

    @Operation(summary = "코스 저장")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseSaveRes>> saveCourse(
        @Valid @RequestBody CourseSaveReq courseSaveReq,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(COURSE_SAVE_SUCCESS, courseSaveService.saveCourse(courseSaveReq, authUserDetails)));
    }

}
