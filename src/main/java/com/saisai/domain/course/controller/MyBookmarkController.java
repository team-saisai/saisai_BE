package com.saisai.domain.course.controller;

import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_EDIT_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.COURSE_BOOKMARK_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.constant.CourseType;
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
import org.springframework.data.domain.Sort;
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

    @Operation(summary = "저장한 코스 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CoursePageRes>>> removeBookmarks(
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "challenge(챌린지 코스), general(일반 코스)") @RequestParam(defaultValue = "challenge") String type,
        @Parameter(description = "levelAsc(난이도 낮은 순), levelDesc(난이도 높은 순), participantsDesc(참가자 순), endSoon(종료일 순)") @RequestParam(defaultValue = "levelAsc") String sort,
        @Auth AuthUserDetails authUserDetails
    ) {
        CourseType courseType = CourseType.from(type);
        CourseSortOption sortOption = CourseSortOption.from(sort);

        Pageable pageable = PageRequest.of(
            page - 1,
            10,
            Sort.by(sortOption.getDirection(), sortOption.getSortColumn())
        );

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(COURSE_BOOKMARK_GET_SUCCESS, courseBookmarkService.getBookmarkCourses(pageable, courseType, sortOption, authUserDetails)));
    }

}
