package com.saisai.domain.ride.controller;

import static com.saisai.domain.common.response.SuccessCode.RIDE_START_SUCCESS;

import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.ride.dto.response.RideStartRes;
import com.saisai.domain.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses/{courseId}/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @Operation(summary = "코스 라이딩 시작",
        description = "총 거리(km), gpx 경로(위도, 경도, 고도, 앞뒤 좌표 간의 거리(m), 현재-시작 좌표 간의 거리(km)")
    @GetMapping
    public ResponseEntity<ApiResponse<RideStartRes>> startRide(
        @PathVariable Long courseId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RIDE_START_SUCCESS, rideService.startRide(courseId)));
    }
}
