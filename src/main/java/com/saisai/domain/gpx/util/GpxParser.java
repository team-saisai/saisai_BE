package com.saisai.domain.gpx.util;

import static com.saisai.domain.common.exception.ExceptionCode.GPX_DOWNLOAD_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_EMPTY;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_PARSING_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_UNKNOWN_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.dto.format.Gpx;
import com.saisai.domain.gpx.dto.format.Track;
import com.saisai.domain.gpx.dto.format.TrackPoint;
import com.saisai.domain.gpx.dto.format.TrackSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpxParser {

    private final RestClient restClient;
    private final XmlMapper xmlMapper;

    public List<GpxPoint> parseGpxpath(String gpxpathUrl) throws CustomException {
        try {
            String gpxContent = restClient.get()
                .uri(gpxpathUrl)
                .retrieve()
                .body(String.class);

            Gpx gpx = xmlMapper.readValue(gpxContent, Gpx.class);
            if (gpx.tracks().isEmpty()) {
                throw new CustomException(GPX_EMPTY);
            }
            return convertGpxToGpxPoints(gpx);

        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_DOWNLOAD_FAILED);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_PARSING_FAILED);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_UNKNOWN_ERROR);
        }
    }

    private List<GpxPoint> convertGpxToGpxPoints(Gpx gpx) {
        List<GpxPoint> gpxPoints = new ArrayList<>();

        double preLat = 0.0;
        double preLon = 0.0;
        boolean isFirstPoint = true;

        for (Track track: gpx.tracks()) {
            if (track.trackSegments().isEmpty()) continue;

            for (TrackSegment segment: track.trackSegments()) {
                if (segment.trackPoints().isEmpty()) continue;

                for (TrackPoint trackPoint: segment.trackPoints()) {
                    double currentLat = trackPoint.lat();
                    double currentLon = trackPoint.lon();
                    double ele = trackPoint.ele();

                    double segmentDistance = 0.0;
                    if (!isFirstPoint) {
                        segmentDistance = DistanceUtils.calculateDistance(preLat, preLon, currentLat, currentLon);
                    } else {
                        isFirstPoint = false;
                    }

                    gpxPoints.add(new GpxPoint(currentLat, currentLon, ele, segmentDistance));

                    preLat = currentLat;
                    preLon = currentLon;
                }
            }
        }
        return gpxPoints;
    }

    // Track -> segment -> point 구조를 스트림으로 평탄화하는 메서드
    private Stream<TrackPoint> flattenTrackPoints(Gpx gpx) {
        return gpx.tracks().stream()
            .flatMap(track -> track.trackSegments().stream())
            .flatMap(segment -> segment.trackPoints().stream());
    }

}
