package com.saisai.domain.auth.controller;

import static com.saisai.domain.common.response.SuccessCode.LOGIN_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.REGISTER_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.REISSUE_SUCCESS;

import com.saisai.domain.auth.dto.request.LoginReq;
import com.saisai.domain.auth.dto.request.RegisterReq;
import com.saisai.domain.auth.dto.response.TokenRes;
import com.saisai.domain.auth.service.AuthService;
import com.saisai.domain.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "회원가입",
        description = "이메일, 닉네임, 비밀번호, 유저롤(USER/ADMIN 입력 필요")
    public ResponseEntity<ApiResponse<TokenRes>> register(
        @Valid @RequestBody RegisterReq registerReq
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(REGISTER_SUCCESS, authService.register(registerReq)));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인",
        description = "이메일, 비밀번호 입력 필요")
    public ResponseEntity<ApiResponse<TokenRes>> login(
        @Valid @RequestBody LoginReq loginReq
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(LOGIN_SUCCESS, authService.login(loginReq)));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급",
        description = "Authorize 버튼 Refresh 토큰 필요 => Bearer 제외 후 토큰만 입력 필요 \n\n" +
            "Authorization(헤더 파라미터) Refresh 토큰 필요 => bearer ~~ 입력 필요\n\n" +
            "PostMan에서는 Header 혹은 Authorization(bearer Token) 둘 중 한 군데에서만 입력해도 됨"
    )
    public ResponseEntity<ApiResponse<TokenRes>> reissueToken(
        @RequestHeader("Authorization") String refreshTokenHeader
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(REISSUE_SUCCESS, authService.reissue(refreshTokenHeader)));
    }
}
