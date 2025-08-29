package com.saisai.domain.course.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION_FOR_COURSE_TYPE;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.challenge.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.checkpoint.client.CheckpointS3;
import com.saisai.domain.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.checkpoint.service.CheckpointJsonParser;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.constant.CourseType;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.response.CourseDetailsRes;
import com.saisai.domain.course.dto.response.CoursePageRes;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.client.GpxS3;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.service.GpxCacheService;
import com.saisai.domain.gpx.service.GpxParser;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import com.saisai.domain.reward.util.RewardUtils;
import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.dto.response.RideResumeRes;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.infra.aws.s3.ImageUtil;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final ChallengeRepository challengeRepository;
    private final CheckpointJsonParser checkpointJsonParser;
    private final ImageUtil imageUtil;
    private final CheckpointS3 checkpointS3;
    private final GpxCacheService gpxCacheService;
    private final GpxS3 gpxS3;
    private final GpxParser gpxParser;

    // 코스 목록 조회 메서드
    public Page<CoursePageRes> getCourses(Pageable pageable, CourseType type, CourseSortOption sortOption, AuthUserDetails authUserDetails) {

        return switch (type) {
            case CHALLENGE -> fetchChallengeCoursesAsPage(pageable, sortOption, authUserDetails.userId());
            case GENERAL -> fetchGeneralCoursesAsPage(pageable, sortOption, authUserDetails.userId());
        };
    }

    // 일반 코스 조회
    private Page<CoursePageRes> fetchGeneralCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {

        if (sortOption.equals(CourseSortOption.END_SOON)) {
            throw new CustomException(INVALID_SORT_OPTION_FOR_COURSE_TYPE);
        }

        Page<GeneralCourseProjection> generalPage = courseRepository.findGeneralCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = generalPage.getContent().stream()
            .map(projection ->
                CoursePageRes.from(
                    projection,
                    imageUtil.getImageUrl(projection.imageUrl())
                ))
            .toList();
        return new PageImpl<>(result, pageable, generalPage.getTotalElements());
    }

    // 챌린지 코스 조회
    private Page<CoursePageRes> fetchChallengeCoursesAsPage(Pageable pageable, CourseSortOption sortOption, Long userId) {
        Page<ChallengeCourseProjection> challengePage = challengeRepository.findChallengeCourses(pageable, sortOption, userId);
        List<CoursePageRes> result = challengePage.getContent().stream()
            .map(projection -> {
                String imageUrl = imageUtil.getImageUrl(projection.imageUrl());
                boolean isEventActive = isRewardEventActive(projection.rewardEventProjection());
                int reward = calculateReward(projection, isEventActive);

                return CoursePageRes.from(projection, imageUrl, isEventActive, reward);
            })
            .toList();
        return new PageImpl<>(result, pageable, challengePage.getTotalElements());
    }

    // 코스 상세 조회 비즈니스 로직
    public CourseDetailsRes getCourseInfo(Long courseId, AuthUserDetails authUserDetails) {
        CourseDetailsProjection course = courseRepository.findCourseDetailsProjection(courseId, authUserDetails.userId())
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        Optional<RideResumeRes> rideResumeRes = rideRepository.findActiveRideIdByUserIdAndCourseId(authUserDetails.userId(), courseId);

        RideCountRes rideCountRes = rideRepository.countRideByCourseId(courseId);

        String checkpointContent = checkpointS3.getCheckpointContent(course.checkpointPath());
        List<Checkpoint> checkpoint = checkpointJsonParser.deserialize(checkpointContent);

        List<GpxPoint> mergedGpxPoints;
        if (course.durunubiId() == null) {
            String gpxContent = gpxS3.getGpxContent(course.gpxpath());
            mergedGpxPoints = gpxParser.parseCustomGpxFile(gpxContent);
        } else {
            mergedGpxPoints = gpxCacheService.getMergedGpxPoints(courseId, checkpoint);
        }

        return CourseDetailsRes.from(course, imageUtil.getImageUrl(course.imageUrl()), rideCountRes, mergedGpxPoints,
            checkpoint, rideResumeRes);
    }

    // 이벤트 활성화 확인
    private boolean isRewardEventActive(RewardEventProjection rewardEventProjection) {
        return Optional.ofNullable(rewardEventProjection)
            .map(RewardEventProjection::rewardEventId)
            .isPresent();
    }

    // 리워드 계산
    private int calculateReward(ChallengeCourseProjection challengeCourseProjection, boolean isEventActive) {
        return isEventActive ?
            RewardUtils.calculateEventReward(
                challengeCourseProjection.level(),
                challengeCourseProjection.rewardEventProjection().rewardEventType(),
                challengeCourseProjection.rewardEventProjection().value()) :
            RewardUtils.calculateEventReward(challengeCourseProjection.level());
    }
}
