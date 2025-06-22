package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.course.api.CourseItem;
import java.time.LocalDateTime;

public record CourseInfoRes(
    String courseId,
    String courseName,
    String contents,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String tourInfo,
    String travelerInfo,
    String gpxpath,
    Integer completeUserCount,
    RewardInfo rewardInfo
) {
    public static CourseInfoRes from(CourseItem courseItem, Long completeUserCount, Challenge challenge) {
        return new CourseInfoRes(
            courseItem.courseId(),
            courseItem.courseName(),
            courseItem.contents(),
            courseItem.level(),
            courseItem.distance(),
            courseItem.estimatedTime(),
            courseItem.sigun(),
            courseItem.tourInfo(),
            courseItem.travelerinfo(),
            courseItem.gpxpath(),
            completeUserCount != null ? completeUserCount.intValue() : 0,
            RewardInfo.from(challenge)
        );
    }

    private record RewardInfo(
        String rewardName,
        String rewardImageUrl,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        private static RewardInfo from(Challenge challenge) {
            if (challenge == null) {
                return null;
            }

            return new RewardInfo(
                challenge.getReward().getName(),
                challenge.getReward().getImage(),
                challenge.getStartedAt(),
                challenge.getEndedAt()
            );
        }
    }
}
