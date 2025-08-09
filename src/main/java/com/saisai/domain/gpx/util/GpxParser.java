package com.saisai.domain.gpx.util;

import static com.saisai.domain.common.exception.ExceptionCode.GPX_DOWNLOAD_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_EMPTY;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_NO_FIRST_POINT;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_PARSING_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_UNKNOWN_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.gpx.dto.GpxKeyPoints;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.dto.format.Gpx;
import com.saisai.domain.gpx.dto.format.TrackPoint;
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

    // 두루누비 API에서 제공하는 gpx 파일 다운로드
    public String downloadGpxContent (String gpxUrl) {
        try {
            return restClient.get()
                .uri(gpxUrl)
                .retrieve()
                .body(String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_DOWNLOAD_FAILED);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_UNKNOWN_ERROR);
        }
    }

    // gpx 전체 파싱 메서드
    public List<GpxPoint> parseGpxContent(String gpxContent) throws CustomException {
        Gpx gpx = getGpxFromContent(gpxContent);

        validGpx(gpx);

        return convertGpxToGpxPoints(gpx);
    }

    // 특정 gpx 좌표만 파싱 메서드
    public GpxKeyPoints parseKeyGpxpath(String gpxContent) {
        Gpx gpx = getGpxFromContent(gpxContent);

        List<TrackPoint> trackPoints = validGpx(gpx);

        TrackPoint first = trackPoints.get(0);

        double firstLat = first.lat();
        double firstLon = first.lon();

        double minLat = firstLat;
        double maxLat = firstLat;
        double minLon = firstLon;
        double maxLon = firstLon;

        for (TrackPoint p : trackPoints) {
            double lat = p.lat();
            double lon = p.lon();
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }

        return new GpxKeyPoints(firstLat, firstLon, minLat, minLon, maxLat, maxLon);
    }

    // gpx 파일 내용 (gpxContent) 파싱 -> gpx 클래스로 반환 메서드
    private Gpx getGpxFromContent (String gpxContent) {

        try {
            return xmlMapper.readValue(gpxContent, Gpx.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_PARSING_FAILED);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(GPX_UNKNOWN_ERROR);
        }
    }

    // gpx -> List<GpxPoint> 변환 메서드
    private List<GpxPoint> convertGpxToGpxPoints(Gpx gpx) {
        List<TrackPoint> trackPoints = flattenTrackPoints(gpx).toList();
        List<GpxPoint> gpxPoints = new ArrayList<>();

        TrackPoint prev = trackPoints.get(0);
        double segmentDistance = 0.0;
        double totalDistanceKm = 0.0;

        gpxPoints.add(GpxPoint.from(prev,segmentDistance, totalDistanceKm));

        for (int i = 1; i < trackPoints.size(); i++) {
            TrackPoint current = trackPoints.get(i);
            segmentDistance = DistanceUtils.calculateDistance(
                prev.lat(), prev.lon(),
                current.lat(), current.lon()
            );

            totalDistanceKm += (segmentDistance / 1000.0);

            gpxPoints.add(GpxPoint.from(current, segmentDistance, totalDistanceKm));

            prev = current;
        }

        return gpxPoints;
    }

    // Track -> segment -> point 구조를 스트림으로 평탄화하는 메서드
    private Stream<TrackPoint> flattenTrackPoints(Gpx gpx) {

        if (gpx == null || gpx.tracks() == null || gpx.tracks().isEmpty()) {
            throw new CustomException(GPX_EMPTY);
        }

        return gpx.tracks().stream()
            .flatMap(track -> track.trackSegments().stream())
            .flatMap(segment -> segment.trackPoints().stream());
    }

    // gpx 포인트 존재 여부 검사 메서드
    private List<TrackPoint> validGpx(Gpx gpx) {
        if (gpx == null || gpx.tracks() == null || gpx.tracks().isEmpty()) {
            throw new CustomException(GPX_EMPTY);
        }

        List<TrackPoint> points = flattenTrackPoints(gpx).toList();
        if (points.isEmpty()) {
            throw new CustomException(GPX_NO_FIRST_POINT);
        }

        return points;
    }
}
