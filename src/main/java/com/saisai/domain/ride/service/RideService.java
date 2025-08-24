package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.CHECKPOINT_INDEX_OUT_OF_RANGE;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_CHECKPOINT_INVALID;
import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.INSUFFICIENT_CHECKPOINT_COUNT;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_ALREADY_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_UNAUTHORIZED_ACCESS;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;
import static java.lang.Boolean.TRUE;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.checkpoint.client.CheckpointS3;
import com.saisai.domain.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.checkpoint.service.CheckpointJsonParser;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.service.GpxCacheService;
import com.saisai.domain.mission.service.MissionService;
import com.saisai.domain.reward.dto.projection.RewardInfo;
import com.saisai.domain.reward.repository.RewardEventRepository;
import com.saisai.domain.reward.service.UserRewardService;
import com.saisai.domain.ride.dto.request.RideCompleteReq;
import com.saisai.domain.ride.dto.request.RideRecordReq;
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
    private final CheckpointS3 checkpointS3;
    private final CheckpointJsonParser checkpointJsonParser;
    private final GpxCacheService gpxCacheService;
    private final MissionService missionService;
    private final UserRewardService userRewardService;
    private final RewardEventRepository rewardEventRepository;


    private static final Set<Long> ADMIN_USER_IDS = Set.of(1L, 2L, 53L, 54L);

    // Ride 시작 데이터 저장
    @Transactional
    public RideStartRes startRide(Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        validateUserNotRiding(user.getId());

        Ride ride = findOrCreateOrResumeRide(user, course);

        List<Checkpoint> checkpoints = getCheckpoint(ride);

        List<GpxPoint> mergeGpxPoints = gpxCacheService.getMergedGpxPoints(courseId, checkpoints);

        return RideStartRes.from(ride, ride.getCourse(), mergeGpxPoints, checkpoints);
    }

    // Ride 중단
    @Transactional
    public RidePausedRes pausedRide(Long rideId, AuthUserDetails authUserDetails, RideRecordReq rideRecordReq) {

        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        validateRideAccess(ride, authUserDetails.userId());

        if (ride.getCourse().getCheckpointCount() <= rideRecordReq.checkpointIdx()+1) {
            throw new CustomException(CHECKPOINT_INDEX_OUT_OF_RANGE);
        }

        int progressRate = calculateProgressRate(rideRecordReq, ride);

        ride.paused(progressRate, rideRecordReq);

        return RidePausedRes.from(ride);
    }

    // 라이딩 재개
    @Transactional
    public RideResumeRes resumeRide(Long rideId, AuthUserDetails authUserDetails) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        validateRideAccess(ride, authUserDetails.userId());
        if (!ride.getStatus().equals(RideStatus.IN_PROGRESS)) {
            validateUserNotRiding(authUserDetails.userId());
        }

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

        // 체크포인트 수 체크
        if (ride.getCheckpointIdx() + 1 != ride.getCourse().getCheckpointCount()) {
            throw new CustomException(INSUFFICIENT_CHECKPOINT_COUNT);
        }

        ride.complete(rideCompleteReq);
        ride.getUser().updateRidingStatus();

        Optional<RewardInfo> rewardInfoOptional = getRewardInfo(rideId);

        if (rewardInfoOptional.isPresent()) {
            userRewardService.earnReward(authUserDetails.userId(), rewardInfoOptional.get());
        }
        missionService.checkAndGrantAllMissions(ride.getUser());
    }

    // 기록 동기화
    @Transactional
    public void syncRideRecord(Long rideId, RideRecordReq rideRecordReq, AuthUserDetails authUserDetails) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new CustomException(RIDE_NOT_FOUND));

        validateRideAccess(ride, authUserDetails.userId());

        if (ride.getCourse().getCheckpointCount() < rideRecordReq.checkpointIdx()+1) {
            throw new CustomException(CHECKPOINT_INDEX_OUT_OF_RANGE);
        }

        int progressRate = calculateProgressRate(rideRecordReq, ride);

        ride.sync(rideRecordReq, progressRate);
    }

    // checkpoint 조회
    private List<Checkpoint> getCheckpoint(Ride ride) {
        String checkpointContent = checkpointS3.getCheckpointContent(ride.getCourse().getCheckpointGpxPath());
        return checkpointJsonParser.deserialize(checkpointContent);
    }

    // 관리자 계정인지 검사
    private boolean isAdminUser(Long userId) {
        return ADMIN_USER_IDS.contains(userId);
    }

    // 라이딩 중인 코스가 있는지 검사
    private void validateUserNotRiding(Long userId) {

        if (isAdminUser(userId)) return;
        Boolean isRiding = rideRepository.existsByUserIdAndStatus(userId, RideStatus.IN_PROGRESS);
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
    private int calculateProgressRate(RideRecordReq rideRecordReq, Ride ride) {
        if (rideRecordReq.checkpointIdx() < 0) {
            return 0;
        }

        Integer courseCheckpointCount = ride.getCourse().getCheckpointCount();
        Integer currentCheckpointCount = rideRecordReq.checkpointIdx();

        if (courseCheckpointCount == null) {
            throw new CustomException(COURSE_CHECKPOINT_INVALID);
        }

        if (currentCheckpointCount == null) {
            throw new CustomException(COURSE_CHECKPOINT_INVALID);
        }

        // 주행률 계산 (최대 100%로 제한)
        double progressRate = Math.min((double) currentCheckpointCount / courseCheckpointCount * 100, 100.0);

        return (int) Math.round(progressRate);
    }

    // ride 생성하거나 재시작 결정
    private Ride findOrCreateOrResumeRide(User user, Course course) {
        Optional<Ride> pausedRide = rideRepository.findByUserIdAndCourseIdAndStatus(user.getId(), course.getId(), RideStatus.PAUSED);

        if (pausedRide.isPresent()) {
            Ride ride = pausedRide.get();
            ride.resume(); // 이미 존재하는 Ride를 재개
            return ride;
        } else {
            Ride newRide = Ride.start(user, course);
            return rideRepository.save(newRide); // 새로운 Ride 생성 및 저장
        }
    }

    private Optional<RewardInfo> getRewardInfo (Long rideId) {
        return rewardEventRepository.findRewardInfoByRideId(rideId);
    }
}
