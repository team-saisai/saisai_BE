package com.saisai.infra.gpx.dto;

public record GpxKeyPoints(
    Double startLat,
    Double startLon,
    Double minLat,
    Double minLon,
    Double maxLat,
    Double maxLon
) {

}
