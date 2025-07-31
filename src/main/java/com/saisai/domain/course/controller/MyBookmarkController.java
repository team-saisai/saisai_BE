package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_EDIT_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.request.BookmarksRemoveReq;
import com.saisai.domain.course.service.CourseBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "코스 북마크(마이페이지) API")
@RestController
@RequestMapping("/api/my/bookmarks/courses")
@RequiredArgsConstructor
public class MyBookmarkController {

    private final CourseBookmarkService courseBookmarkService;

    @Operation(description = "북마크 선택 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeBookmarks(
        @RequestBody BookmarksRemoveReq bookmarksRemoveReq,
        @Auth AuthUserDetails authUserDetails
    ) {

        courseBookmarkService.removeBookmarks(bookmarksRemoveReq, authUserDetails);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_BOOKMARK_EDIT_SUCCESS));
    }

}
