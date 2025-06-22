package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.entity.CourseImage;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CourseListItemRes(
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    ChallengeInfo challengeInfo
    // 도전자 수, 완주자 수
) {

    public static CourseListItemRes from(CourseItem courseItem, Challenge challenge, CourseImage courseImage) {
        String imageUrl = null;
        if (courseImage != null) {
            imageUrl = courseImage.getUrl();
        }

        return new CourseListItemRes(
            courseItem.courseName(),
            courseItem.summary(),
            courseItem.level(),
            courseItem.distance(),
            courseItem.estimatedTime(),
            courseItem.sigun(),
            imageUrl,
            CourseListItemRes.ChallengeInfo.from(challenge)
        );
    }

    private record ChallengeInfo(
        ChallengeStatus challengeStatus,
        LocalDateTime challengeEndedTime
    ) {
        private static ChallengeInfo from(Challenge challenge) {
            if (challenge == null) return null;

            return new ChallengeInfo(
                challenge.getStatus(),
                challenge.getEndedAt()
            );
        }
    }
}
