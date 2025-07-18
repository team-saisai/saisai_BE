package com.saisai.domain.reward.controller;

import static com.saisai.domain.common.response.SuccessCode.MY_REWARD_LIST_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.reward.dto.response.UserRewardInfroRes;
import com.saisai.domain.reward.service.UserRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리워드(개인) API")
@RestController
@RequestMapping("/api/my/rewards")
@RequiredArgsConstructor
public class UserRewardController {

    private final UserRewardService userRewardService;

    @GetMapping
    @Operation(summary = "획득 리워드 전체 목록 조회")
    public ResponseEntity<ApiResponse<UserRewardInfroRes>> getMyRewards(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(MY_REWARD_LIST_GET_SUCCESS, userRewardService.getMyRewards(authUserDetails)));
    }
}
