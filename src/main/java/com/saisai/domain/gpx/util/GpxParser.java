package com.saisai.domain.gpx.util;

import static com.saisai.domain.common.exception.ExceptionCode.GPX_DOWNLOAD_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_EMPTY;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_NO_FIRST_POINT;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_PARSING_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_UNKNOWN_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.gpx.dto.FirstGpxPoint;
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

    // 첫번째 gpx 좌표만 파싱 메서드
    public FirstGpxPoint parseFirstGpxpath(String gpxContent) throws CustomException {
        Gpx gpx = getGpxFromContent(gpxContent);

        validGpx(gpx);

        TrackPoint firstTrackPoint = flattenTrackPoints(gpx)
            .findFirst()
            .orElseThrow(() -> new CustomException(GPX_NO_FIRST_POINT));

        return new FirstGpxPoint(firstTrackPoint.lat(), firstTrackPoint.lon());
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
        gpxPoints.add(GpxPoint.from(prev, 0.0));

        for (int i = 1; i < trackPoints.size(); i++) {
            TrackPoint current = trackPoints.get(i);
            double segmentDistance = DistanceUtils.calculateDistance(
                prev.lat(), prev.lon(),
                current.lat(), current.lon()
            );

            gpxPoints.add(GpxPoint.from(current, segmentDistance));

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
    private void validGpx(Gpx gpx) {
        if (gpx.tracks() == null || gpx.tracks().isEmpty()) {
            throw new CustomException(GPX_EMPTY);
        }

        boolean hasPoints = flattenTrackPoints(gpx).findAny().isPresent();
        if (!hasPoints) {
            throw new CustomException(GPX_NO_FIRST_POINT);
        }
    }
}
