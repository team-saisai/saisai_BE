package com.saisai.domain.ride.controller;

import static com.saisai.domain.common.response.SuccessCode.MY_RIDE_RECORD_GET_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.RECENT_RIDE_COURSE_GET_SUCCESS;
import static com.saisai.domain.common.response.SuccessCode.RIDE_DELETE_SUCCESS;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.annotation.Auth;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.ride.constant.RideSortOption;
import com.saisai.domain.ride.dto.request.RideDeleteReq;
import com.saisai.domain.ride.dto.response.RecentRideInfoRes;
import com.saisai.domain.ride.dto.response.RideDeleteRes;
import com.saisai.domain.ride.dto.response.RideRecordRes;
import com.saisai.domain.ride.service.MyRideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "라이딩(개인) API")
@RestController
@RequestMapping("/api/my/rides")
@RequiredArgsConstructor
public class MyRideController {

    private final MyRideService myRideService;

    @Operation(summary = "최근 라이딩 한 코스 조회",
        description = "코스ID, 코스명, 시군, 코스 이미지, 최근 주행일, 총 거리(km), 완주율 반환")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<RecentRideInfoRes>> getRecentRideInfo(
        @Auth AuthUserDetails authUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RECENT_RIDE_COURSE_GET_SUCCESS, myRideService.getRecentRideInfo(authUserDetails)));
    }

    @Operation(summary = "나의 기록 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<RideDeleteRes>> deleteRides(
        @Auth AuthUserDetails authUserDetails,
        @RequestBody RideDeleteReq rideDeleteReq
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(RIDE_DELETE_SUCCESS, myRideService.deleteRides(authUserDetails, rideDeleteReq)));
    }

    @Operation(summary = "나의 기록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RideRecordRes>>> getMyRideRecords(
        @Parameter(description = "페이지 번호")
        @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "newest(최신순), ordest(오래된 순)")
        @RequestParam(defaultValue = "newest") String sort,
        @Parameter(description = "주행 중인 코스만 보기(true/false)")
        @RequestParam(defaultValue = "false") Boolean ridingCourseOnly,
        @Auth AuthUserDetails authUserDetails
    ) {
        RideSortOption sortOption = RideSortOption.from(sort);

        Pageable pageable = PageRequest.of(
            page - 1,
            10,
            Sort.by(sortOption.getDirection(), sortOption.getSortColumn())
        );

        Page<RideRecordRes> dto = myRideService.getMyRideRecords(pageable, sortOption,
            ridingCourseOnly, authUserDetails);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(MY_RIDE_RECORD_GET_SUCCESS, dto));
    }
}
