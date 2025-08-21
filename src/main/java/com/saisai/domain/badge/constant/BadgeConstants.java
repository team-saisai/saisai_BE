package com.saisai.domain.badge.constant;

import java.util.Set;

public class BadgeConstants {

    public static final String FIRST_COURSE_COMPLETE = "천리 길도 한 바퀴부터";
    public static final String TOTAL_COURSE_30_COMPLETE = "천하제일라이더";
    public static final String UNIQUE_REGION_5_COMPLETE = "강호도장깨기";
    public static final String CONSECUTIVE_RIDING_3_DAYS = "자주 보는 사이";
    public static final String HARD_COURSE_10_COMPLETE = "상상이상";

    public static final int HARD_COURSE_LEVEL = 3;


    public static final Set<String> ALL_MISSION_BADGE_NAMES = Set.of(
        FIRST_COURSE_COMPLETE,
        TOTAL_COURSE_30_COMPLETE,
        UNIQUE_REGION_5_COMPLETE,
        CONSECUTIVE_RIDING_3_DAYS,
        HARD_COURSE_10_COMPLETE
    );

    private BadgeConstants() {}
}
