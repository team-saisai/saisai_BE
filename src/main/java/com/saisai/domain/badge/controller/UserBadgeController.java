package com.saisai.domain.badge.controller;

import static com.saisai.domain.common.response.SuccessCode.MY_BADGE_LIST_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.badge.dto.response.BadgeSummaryRes;
import com.saisai.domain.badge.service.BadgeService;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "뱃지(개인) API")
@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class UserBadgeController {

    private final BadgeService badgeService;

    @GetMapping("/me")
    @Operation(summary = "사용자가 소유한 뱃지 목록")
    public ResponseEntity<ApiResponse<List<BadgeSummaryRes>>> getMyBadgeList(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(MY_BADGE_LIST_GET_SUCCESS, badgeService.getMyBadgeList(authUserDetails)));
    }

}
