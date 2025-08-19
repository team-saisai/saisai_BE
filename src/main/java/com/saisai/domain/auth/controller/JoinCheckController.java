package com.saisai.domain.auth.controller;

import static com.saisai.domain.common.response.SuccessCode.JOIN_CHECK_SUCCESS;

import com.saisai.domain.auth.dto.request.JoinCheckReq;
import com.saisai.domain.auth.dto.response.JoinCheckRes;
import com.saisai.domain.auth.service.JoinCheckService;
import com.saisai.domain.common.response.ApiResponse;
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

@Tag(name = "회원가입 체크 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JoinCheckController {

    private final JoinCheckService joinCheckService;

    @PostMapping("/kakao/isJoin")
    @Operation(summary = "새로운 유저인지 확인")
    public ResponseEntity<ApiResponse<JoinCheckRes>> checkJoinforKakao(
        @Valid @RequestBody JoinCheckReq joinCheckReq
    ) {
        JoinCheckRes response = joinCheckService.checkJoinForKakao(joinCheckReq);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(JOIN_CHECK_SUCCESS, response));
    }

    @PostMapping("/google/android/isJoin")
    @Operation(summary = "새로운 유저인지 확인")
    public ResponseEntity<ApiResponse<JoinCheckRes>> checkJoinforGoogleAndroid(
        @Valid @RequestBody JoinCheckReq joinCheckReq
    ) {
        JoinCheckRes response = joinCheckService.checkJoinForGooleAndroid(joinCheckReq);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(JOIN_CHECK_SUCCESS, response));
    }

    @PostMapping("/google/ios/isJoin")
    @Operation(summary = "새로운 유저인지 확인")
    public ResponseEntity<ApiResponse<JoinCheckRes>> checkJoinforGoogleIos(
        @Valid @RequestBody JoinCheckReq joinCheckReq
    ) {
        JoinCheckRes response = joinCheckService.checkJoinForGooleIos(joinCheckReq);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(JOIN_CHECK_SUCCESS, response));
    }

    @PostMapping("/google/apple/isJoin")
    @Operation(summary = "새로운 유저인지 확인")
    public ResponseEntity<ApiResponse<JoinCheckRes>> checkJoinforApple(
        @Valid @RequestBody JoinCheckReq joinCheckReq
    ) {
        JoinCheckRes response = joinCheckService.checkJoinForApple(joinCheckReq);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(JOIN_CHECK_SUCCESS, response));
    }
}
