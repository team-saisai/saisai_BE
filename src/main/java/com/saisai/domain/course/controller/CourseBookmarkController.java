package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_ADD_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_REMOVE_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.response.CourseBookmarkRes;
import com.saisai.domain.course.service.CourseBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코스 북마크 API")
@RestController
@RequestMapping("/api/courses/{courseId}/bookmarks")
@RequiredArgsConstructor
public class CourseBookmarkController {

    private final CourseBookmarkService courseBookMarkService;

    @Operation(summary = "코스 북마크 추가")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseBookmarkRes>> bookmarkCourse(
        @PathVariable Long courseId,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(COURSE_BOOKMARK_ADD_SUCCESS, courseBookMarkService.bookmarkCourse(courseId, authUserDetails)));
    }

    @Operation(summary = "코스 북마크 취소")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<CourseBookmarkRes>> removeBookmark(
        @PathVariable Long courseId,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_BOOKMARK_REMOVE_SUCCESS,courseBookMarkService.removeBookmark(courseId, authUserDetails)));
    }

}
