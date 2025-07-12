package com.saisai.domain.ride.controller;

import static com.saisai.domain.common.response.SuccessCode.RIDE_COMPLETE_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.RIDE_PAUSED_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.RIDE_START_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.ride.dto.request.RideCompleteReq;
import com.saisai.domain.ride.dto.request.RidePausedReq;
import com.saisai.domain.ride.dto.response.RidePausedRes;
import com.saisai.domain.ride.dto.response.RideStartRes;
import com.saisai.domain.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "라이딩 API", description = "테스트용 계정은 라이딩 상태 예외처리 무시(ex. 라이딩 중단 상태에서 중단 요청 시 발생하는 예외처리 무시)\n\n" + "계정 생성 후 요청주시면 해당 계정 추가하겠습니다.(7월9일 자까지는 확인 완료)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @Operation(summary = "코스 라이딩 시작",
        description = "총 거리(km), gpx 경로(위도, 경도, 고도, 앞뒤 좌표 간의 거리(m), 현재까지의 누적거리(km)")
    @PostMapping("/courses/{courseId}/rides")
    public ResponseEntity<ApiResponse<RideStartRes>> startRide(
        @PathVariable Long courseId,
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RIDE_START_SUCCESS, rideService.startRide(courseId, authUserDetails)));
    }

    @Operation(summary = "코스 라이딩 중단",
        description = "라이딩 시작API로 기록 저장 후 테스트 가능\n\nRequestBody 모든 필드 null 불가")
    @PatchMapping("/rides/{rideId}/pause")
    public ResponseEntity<ApiResponse<RidePausedRes>> pausedRide(
        @PathVariable Long courseId,
        @PathVariable Long rideId,
        @Auth AuthUserDetails authUserDetails,
        @Valid @RequestBody RidePausedReq ridePausedReq
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RIDE_PAUSED_SUCCESS, rideService.pausedRide(courseId, rideId, authUserDetails, ridePausedReq)));
    }

    @Operation(summary = "코스 라이딩 완주",
        description = "소요시간(초 단위, 1 이상 입력), 달린 거리(km단위, 0.1 이상 입력), 코스 완주한 사진 (크기 제한 2mb인데 더 크면 말해주세요.)")
    @PatchMapping(value = "/rides/{rideId}/complete",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> completeRide(
        @PathVariable Long rideId,
        @Valid @ModelAttribute RideCompleteReq completeReq,
        @Auth AuthUserDetails authUserDetails
    ) {
        rideService.completeRide(rideId, completeReq, authUserDetails);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RIDE_COMPLETE_SUCCESS));
    }
}
