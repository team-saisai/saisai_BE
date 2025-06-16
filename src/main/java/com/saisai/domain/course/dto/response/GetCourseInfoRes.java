package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.common.exception.CustomException;
import java.time.LocalDateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public record GetCourseInfoRes(
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

    public static GetCourseInfoRes createGetCourseInfoRes(
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
        return new GetCourseInfoRes(
            courseId,
            courseName,
            contents,
            level,
            distance,
            estimatedTime,
            sigun,
            tourInfo,
            travelerInfo,
            gpxpath,
            completeUserCount != null ? completeUserCount.intValue() : 0, // Long -> Integer 변환 및 null 처리
            rewardInfo // Reward가 null이 아니면 RewardInfo 생성, 아니면 null
        );
    }

    public static GetCourseInfoRes createCourseInfo(JSONArray courseItem, Long completeUserCount, Challenge challenge) throws CustomException {
        try {
            JSONObject course = (JSONObject) courseItem.get(0);

            String courseId = (String)course.get("crsIdx");
            String courseName = (String)course.get("crsKorNm");
            String contents = (String)course.get("crsContents");
            String levelStr = (String)course.get("crsLevel");
            Integer level = Integer.parseInt(levelStr);
            String distanceStr = (String)course.get("crsDstnc");
            Double distance = Double.parseDouble(distanceStr);
            String estimatedTimeStr = (String)course.get("crsTotlRqrmHour");
            Double estimatedTime = Double.parseDouble(estimatedTimeStr);
            String sigun = (String)course.get("sigun");
            String tourInfo = (String) course.get("crsTourInfo");
            String travelerInfo = (String) course.get("travlerinfo");
            String gpxpath = (String) course.get("gpxpath");

            return GetCourseInfoRes.createGetCourseInfoRes(
                courseId,
                courseName,
                contents,
                level,
                distance,
                estimatedTime,
                sigun,
                tourInfo,
                travelerInfo,
                gpxpath,
                completeUserCount.intValue(),
                RewardInfo.from(challenge)
            );

        } catch (Exception exception) {
            throw new CustomException(exception);
        }
    }
    public record RewardInfo(
        String rewardName,
        String rewardImageUrl,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        public static RewardInfo from(Challenge challenge) {
            if (challenge == null) {
                return null;
            }

            return new RewardInfo(
                challenge.getReward().getName(),
                challenge.getReward().getImage(),
                challenge.getStartedAt(),
                challenge.getClosedAt()
            );
        }
    }
}
