package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_ALREADY_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;
import static java.lang.Boolean.TRUE;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.aws.s3.GpxS3;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.util.GpxParser;
import com.saisai.domain.ride.dto.response.RideStartRes;
import com.saisai.domain.ride.entity.Ride;
import com.saisai.domain.ride.entity.RideStatus;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GpxS3 gpxS3;
    private final GpxParser gpxParser;

    private static final Set<Long> ADMIN_USER_IDS = Set.of(1L, 2L, 53L);

    // Ride 시작 데이터 저장
    @Transactional
    public RideStartRes startRide(Long courseId, AuthUserDetails authUserDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!isAdminUser(user.getId())) {
            validateUserNotRiding(user);
        }

        Ride ride = Ride.start(user, course);
        Ride saveRide = rideRepository.save(ride);

        List<GpxPoint> gpxPoints = getGpxPoints(saveRide);

        return RideStartRes.from(saveRide.getCourse().getDistance(), gpxPoints);
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
}
