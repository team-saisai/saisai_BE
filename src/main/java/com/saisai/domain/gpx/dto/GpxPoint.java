package com.saisai.domain.gpx.dto;


public record GpxPoint(
    Double latitude,
    Double longitude,
    Double elevation,
    Double segmentDistance
) {

}
