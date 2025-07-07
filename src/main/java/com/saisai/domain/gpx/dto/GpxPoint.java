package com.saisai.domain.gpx.dto;


import com.saisai.domain.gpx.dto.format.TrackPoint;

public record GpxPoint(
    Double latitude,
    Double longitude,
    Double elevation,
    Double segmentDistance,
    Double totalDistance
) {
    public static GpxPoint from(TrackPoint trackPoint, double segmentDistance, double totalDistance) {
        return new GpxPoint(
            trackPoint.lat(),
            trackPoint.lon(),
            trackPoint.ele(),
            segmentDistance,
            totalDistance
        );
    }
}
