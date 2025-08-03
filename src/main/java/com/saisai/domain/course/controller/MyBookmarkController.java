package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_EDIT_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.request.BookmarksRemoveReq;
import com.saisai.domain.course.dto.response.BookmarksRemoveRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.service.CourseBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코스 북마크(마이페이지) API")
@RestController
@RequestMapping("/api/my/bookmarks/courses")
@RequiredArgsConstructor
public class MyBookmarkController {

    private final CourseBookmarkService courseBookmarkService;

    @Operation(summary = "북마크 선택 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<BookmarksRemoveRes>> removeBookmarks(
        @RequestBody BookmarksRemoveReq bookmarksRemoveReq,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_BOOKMARK_EDIT_SUCCESS, courseBookmarkService.removeBookmarks(bookmarksRemoveReq, authUserDetails)));
    }

    @Operation(summary = "저장한 코스 조회",
    description = "최근 저장한 순으로 정렬\n\n코스 조회 response 구조와 동일\n\n챌린지가 아닌 코스는 (challengeStatus, challengeEndedAt, isEventActive, reward) = null")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CoursePageRes>>> removeBookmarks(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
        @Auth AuthUserDetails authUserDetails
    ) {
        Pageable pageable = PageRequest.of(
            page - 1,
            10
        );

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_BOOKMARK_GET_SUCCESS, courseBookmarkService.getBookmarkCourses(pageable, authUserDetails)));
    }

}
