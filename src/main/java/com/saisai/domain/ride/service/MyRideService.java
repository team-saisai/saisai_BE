package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.ImageUtil;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.repository.CourseRepository;
import com.saisai.domain.ride.dto.response.RecentRideInfoRes;
import com.saisai.domain.ride.entity.Ride;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyRideService {

    private final RideRepository rideRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ImageUtil imageUtil;

    public RecentRideInfoRes getRecentRideInfo(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Ride recentRide = rideRepository.findTop1ByUserIdOrderByModifiedAtDesc(user.getId());

        if (recentRide == null) {
            return RecentRideInfoRes.empty();
        }

        Course course = courseRepository.findById(recentRide.getCourse().getId())
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        String courseImageUrl = imageUtil.getImageUrl(course.getImage());

        return RecentRideInfoRes.from(recentRide, course, courseImageUrl);
    }
}
