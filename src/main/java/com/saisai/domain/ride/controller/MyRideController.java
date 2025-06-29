package com.saisai.domain.ride.controller;

import static com.saisai.domain.common.response.SuccessCode.RECENT_RIDE_COURSE_GET_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.ride.dto.response.RecentRideInfoRes;
import com.saisai.domain.ride.service.MyRideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "라이딩(개인) API")
@RestController
@RequestMapping("/api/my/rides")
@RequiredArgsConstructor
public class MyRideController {

    private final MyRideService myRideService;

    @Operation(summary = "최근 라이딩 한 코스 조회",
        description = "코스명, 시군, 코스 이미지, 최근 주행일, 총 거리(km), 완주율 반환")
    @GetMapping
    public ResponseEntity<ApiResponse<RecentRideInfoRes>> getRecentRideInfo(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RECENT_RIDE_COURSE_GET_SUCCESS, myRideService.getRecentRideInfo(authUserDetails)));
    }
}
