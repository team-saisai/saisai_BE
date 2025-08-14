package com.saisai.domain.user.controller;

import static com.saisai.domain.common.response.SuccessCode.MYPAGE_INFO_GET_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.USER_GREETING_INFO_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.user.dto.response.MypageRes;
import com.saisai.domain.user.dto.response.UserGreetingRes;
import com.saisai.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class UserMyController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회(홈화면)")
    @GetMapping
    public ResponseEntity<ApiResponse<UserGreetingRes>> getUserGreetingInfo(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(USER_GREETING_INFO_GET_SUCCESS, userService.getUserGreetingInfo(authUserDetails)));
    }

    @Operation(summary = "유저 정보 조회(마이페이지)")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MypageRes>> getMyPageInfo(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(MYPAGE_INFO_GET_SUCCESS, userService.getMypageInfo(authUserDetails)));
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("profile/nickname/check")
    public ResponseEntity<ApiResponse<UserNicknameRes>> checkNicknameDuplica(
        @RequestParam
        @ValidNickname
        String nickname
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(NICKNAME_DUPLICA_CHECK, userService.checkNicknameDuplica(nickname)));
    }

}
