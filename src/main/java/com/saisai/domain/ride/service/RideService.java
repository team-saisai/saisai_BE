package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_DISTANCE_INVALID;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_ALREADY_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_UNAUTHORIZED_ACCESS;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;
import static java.lang.Boolean.TRUE;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.aws.s3.GpxS3;
import com.saisai.domain.common.aws.s3.ImageUtil;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import com.saisai.domain.ride.dto.request.RideCompleteReq;
import com.saisai.domain.ride.dto.request.RidePausedReq;
import com.saisai.domain.ride.dto.response.RidePausedRes;
import com.saisai.domain.ride.dto.response.RideResumeRes;
import com.saisai.domain.ride.dto.response.RideStartRes;
import com.saisai.domain.ride.entity.Ride;
import com.saisai.domain.ride.entity.RideStatus;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CacheRideService cacheRideService;
    private final GpxS3 gpxS3;
    private final GpxParser gpxParser;
    private final ImageUtil imageUtil;

    private static final Set<Long> ADMIN_USER_IDS = Set.of(1L, 2L, 53L, 54L);

    // Ride 시작 데이터 저장
    @Transactional
    public RideStartRes startRide(Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Ride currentRide;

        if (!isAdminUser(user.getId())) {
            validateUserNotRiding(user);

            Optional<Ride> pausedRideOptional =
                rideRepository.findByUserIdAndCourseIdAndStatus(user.getId(), course.getId(), RideStatus.PAUSED);

            if (pausedRideOptional.isPresent()) {
                currentRide = pausedRideOptional.get();
                currentRide.resume();
            } else {
                currentRide = Ride.start(user, course);
                rideRepository.save(currentRide);
            }
        } else {
            currentRide = Ride.start(user, course);
            rideRepository.save(currentRide);
        }

        List<GpxPoint> gpxPoints = getGpxPoints(currentRide);

        return RideStartRes.from(currentRide, course, gpxPoints);
    }

    // Ride 중단
    @Transactional
    public RidePausedRes pausedRide(Long rideId, AuthUserDetails authUserDetails, RidePausedReq ridePausedReq) {

        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        validateRideAccess(ride, authUserDetails.userId());

        int progressRate = calculateProgressRate(ridePausedReq, ride);

        // 관리자 계정이면 라이딩 상태 예외처리 X
        if (isAdminUser(authUserDetails.userId())) {
            ride.pausedForAdmin(progressRate);
        } else {
            ride.paused(progressRate);
        }

        //cacheRideService.savePausedData(authUserDetails.userId(), rideId, ridePausedReq);

        return RidePausedRes.from(ride);
    }

    // 라이딩 재개
    @Transactional
    public RideResumeRes resumeRide(Long rideId, AuthUserDetails authUserDetails) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        validateRideAccess(ride, authUserDetails.userId());

        ride.resume();

        return RideResumeRes.from(ride);
    }

    // Ride 완주
    @Transactional
    public void completeRide(Long rideId, RideCompleteReq rideCompleteReq, AuthUserDetails authUserDetails) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        // RideId의 user인지 확인
        if (!ride.getUser().getId().equals(authUserDetails.userId())) {
            throw new CustomException(RIDE_UNAUTHORIZED_ACCESS);
        }

        // 달리고 있는 상태인지 확인
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new CustomException(RIDE_NOT_IN_PROGRESS);
        }

        String image = imageUtil.upload(rideCompleteReq.completedImage(), "ride");

        ride.complete(rideCompleteReq, image);
    }


    // ride course Gpx 포인트 조회
    private List<GpxPoint> getGpxPoints(Ride ride) {
        String gpxContent = gpxS3.getGpxContent(ride.getCourse().getGpxPath());
        return gpxParser.parseGpxContent(gpxContent);
    }

    // 관리자 계정인지 검사
    private boolean isAdminUser(Long userId) {
        return ADMIN_USER_IDS.contains(userId);
    }

    // 라이딩 중인 코스가 있는지 검사
    private void validateUserNotRiding(User user) {
        Boolean isRiding = rideRepository.existsByUserAndStatus(user, RideStatus.IN_PROGRESS);
        if (TRUE.equals(isRiding)) {
            throw new CustomException(RIDE_ALREADY_IN_PROGRESS);
        }
    }

    // 라이딩 접근 권한 검사
    private void validateRideAccess(Ride ride, Long userId) {
        // 사용자 권한 검사
        if (!ride.getUser().getId().equals(userId)) {
            throw new CustomException(RIDE_UNAUTHORIZED_ACCESS);
        }
    }

    // 완주율(주행률) 계산
    private int calculateProgressRate(RidePausedReq ridePausedReq, Ride ride) {
        Double courseDistance = ride.getCourse().getDistance();
        Double currentDistance = ridePausedReq.totalDistance();

        if (courseDistance == null || courseDistance <= 0) {
            throw new CustomException(COURSE_DISTANCE_INVALID);
        }

        if (currentDistance == null || currentDistance < 0) {
            throw new CustomException(COURSE_DISTANCE_INVALID);
        }

        // 주행률 계산 (최대 100%로 제한)
        double progressRate = Math.min(currentDistance / courseDistance * 100, 100.0);

        return (int) Math.round(progressRate);
    }
}
