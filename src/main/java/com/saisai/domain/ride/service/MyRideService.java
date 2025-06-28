package com.saisai.domain.ride.service;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.entity.CourseImage;
import com.saisai.domain.course.repository.CourseImageRepository;
import com.saisai.domain.course.service.CourseService;
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
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final CourseImageRepository courseImageRepository;

    public RecentRideInfoRes getRecentRideInfo(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Ride recentRide = rideRepository.findTop1ByUserIdOrderByModifiedAtDesc(user.getId());

        if (recentRide == null) {
            return null;
        }

        CourseItem couresItem = courseService.findCourseByName(recentRide.getCourseName())
            .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));

        CourseImage courseImage = courseImageRepository.findCourseImageByCourseName(recentRide.getCourseName());

        return RecentRideInfoRes.from(recentRide, couresItem, courseImage);
    }
}
