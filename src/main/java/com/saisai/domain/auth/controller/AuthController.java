package com.saisai.domain.auth.controller;

import static com.saisai.domain.common.response.SuccessCode.LOGIN_SUCCESS;

import com.saisai.domain.auth.dto.request.OauthLoginReq;
import com.saisai.domain.auth.dto.response.TokenRes;
import com.saisai.domain.auth.service.AuthService;
import com.saisai.domain.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인 API")
@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    @SecurityRequirements(value = {})
    @Operation(summary = "카카오 소셜 로그인")
    public ResponseEntity<ApiResponse<TokenRes>> kakaoLogin(
        @Valid @RequestBody OauthLoginReq oauthLoginReq
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(LOGIN_SUCCESS, authService.kakaoLogion(oauthLoginReq)));
    }

    @PostMapping("/google/android")
    @SecurityRequirements(value = {})
    @Operation(summary = "구글 소셜 로그인 (안드로이드)")
    public ResponseEntity<ApiResponse<TokenRes>> loginGoogleAndroid(
        @Valid @RequestBody OauthLoginReq oauthLoginReq
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(LOGIN_SUCCESS, authService.googleLoginAndroid(oauthLoginReq)));
    }

    @PostMapping("/google/ios")
    @SecurityRequirements(value = {})
    @Operation(summary = "구글 소셜 로그인 (IOS)")
    public ResponseEntity<ApiResponse<TokenRes>> loginGoogleIos(
        @Valid @RequestBody OauthLoginReq oauthLoginReq
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(LOGIN_SUCCESS, authService.googleLoginIos(oauthLoginReq)));
    }

    @PostMapping("/apple")
    @SecurityRequirements(value = {})
    @Operation(summary = "애플 소셜 로그인")
    public ResponseEntity<ApiResponse<TokenRes>> loginApple(
        @Valid @RequestBody OauthLoginReq oauthLoginReq
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(LOGIN_SUCCESS, authService.appleLogin(oauthLoginReq)));
    }

}
