package com.saisai.domain.badge.controller;

import static com.saisai.domain.common.response.SuccessCode.BADGE_CREATE_SUCCESS;

import com.saisai.domain.badge.dto.request.BadgeRegisterReq;
import com.saisai.domain.badge.dto.response.BadgeRegisterRes;
import com.saisai.domain.badge.service.BadgeService;
import com.saisai.domain.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name= "ADMIN 전용 뱃지 API")
@RequestMapping("/api/admin/badges")
@RestController
@RequiredArgsConstructor
public class AdminBadgeController {

    private final BadgeService badgeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새 뱃지 등록",
        description =
            "ADMIN이 새로운 뱃지를 등록할 때 사용\n\n" +
            "뱃지명, 설명, 이미지 필수 입력값")
    public ResponseEntity<ApiResponse<BadgeRegisterRes>> createBadge(
        @ModelAttribute @Valid BadgeRegisterReq badgeRegisterReq
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(BADGE_CREATE_SUCCESS, badgeService.createBadge(badgeRegisterReq)));

    }
}
