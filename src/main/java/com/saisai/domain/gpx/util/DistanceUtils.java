package com.saisai.domain.gpx.util;

public class DistanceUtils {

    private static final int EARTH_RADIUS_METERS = 6371000; // 지구 반지름

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistanceRad = Math.toRadians(lat2 - lat1);
        double lonDistanceRad = Math.toRadians(lon2 - lon1);

        // 하버사인 'a' : 구면 위 두 점 사이 중심각
        double a = Math.sin(latDistanceRad / 2) * Math.sin(latDistanceRad / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistanceRad / 2) * Math.sin(lonDistanceRad / 2);

        // 하버사인 'c' : 두 지점 사이의 중심각
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 최종 거리: 지구 반지름 * 중심각
        return EARTH_RADIUS_METERS * c;
    }
}
