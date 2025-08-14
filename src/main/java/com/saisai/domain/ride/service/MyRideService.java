package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.ride.constant.RideSortOption;
import com.saisai.domain.ride.dto.request.RideDeleteReq;
import com.saisai.domain.ride.dto.response.RecentRideInfoRes;
import com.saisai.domain.ride.dto.response.RideDeleteRes;
import com.saisai.domain.ride.dto.response.RideRecordRes;
import com.saisai.domain.ride.entity.Ride;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import com.saisai.infra.aws.s3.ImageUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyRideService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ImageUtil imageUtil;

    @Transactional(readOnly = true)
    public RecentRideInfoRes getRecentRideInfo(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Ride recentRide = rideRepository.findTop1ByUserIdOrderByModifiedAtDesc(user.getId());

        if (recentRide == null) {
            return null;
        }

        Course course = courseRepository.findById(recentRide.getCourse().getId())
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        String courseImageUrl = imageUtil.getImageUrl(course.getImage());

        return RecentRideInfoRes.from(recentRide, course, courseImageUrl);
    }

    @Transactional
    public RideDeleteRes deleteRides(AuthUserDetails authUserDetails, RideDeleteReq rideDeleteReq) {

        long deleteCount = rideRepository.markRideAsDeleted(
            authUserDetails.userId(),
            rideDeleteReq.rideIds()
        );

        return RideDeleteRes.of(deleteCount);
    }

    @Transactional(readOnly = true)
    public Page<RideRecordRes> getMyRideRecords(Pageable pageable, RideSortOption sortOption,
                                                Boolean notCompletedOnly, AuthUserDetails authUserDetails)
    {
        Page<RideRecordRes> page = rideRepository.findMyRideRecords(pageable, sortOption, notCompletedOnly, authUserDetails.userId());

        List<RideRecordRes> result = page.getContent().stream()
            .map(dto -> {
                String imageUrl = imageUtil.getImageUrl(dto.imageUrl());
                // 팩토리 메서드를 사용해 새로운 DTO 인스턴스 생성
                return RideRecordRes.from(dto, imageUrl);
            })
            .toList();


        return new PageImpl<>(result, pageable, page.getTotalElements());
    }
}
