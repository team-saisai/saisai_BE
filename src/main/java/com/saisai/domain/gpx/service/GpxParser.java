package com.saisai.domain.gpx.service;

import static com.saisai.domain.common.exception.ExceptionCode.GPX_DOWNLOAD_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_EMPTY;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_FILE_READ_FAIL;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_NOT_ENOUGH_POINTS;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_NO_FIRST_POINT;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_PARSING_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.GPX_UNKNOWN_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.saisai.domain.checkpoint.dto.CheckpointInfo;
import com.saisai.domain.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.gpx.dto.GpxKeyPoints;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.gpx.dto.format.Gpx;
import com.saisai.domain.gpx.dto.format.TrackPoint;
import com.saisai.domain.gpx.util.DistanceUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpxParser {

    private final RestClient restClient;
    private final XmlMapper xmlMapper;

    public String convertGpxToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new CustomException(GPX_FILE_READ_FAIL);
        }
    }

    public List<GpxPoint> parseCustomGpxFile(String gpxContent) {
        List<TrackPoint> trackPoints = parseGpxContent(gpxContent);

        return convertGpxToGpxPoints(trackPoints);
    }

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
    public List<TrackPoint> parseGpxContent(String gpxContent) throws CustomException {
        Gpx gpx = getGpxFromContent(gpxContent);

        return validGpx(gpx);
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

    // Track -> segment -> point 구조를 스트림으로 평탄화하는 메서드
    private Stream<TrackPoint> flattenTrackPoints(Gpx gpx) {

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


    /**
     * TrackPoint와 Checkpoint를 병합하여 GpxPoint 리스트 생성
     * 1. 모든 체크포인트의 삽입 위치 계산
     * 2. 인덱스 역순 정렬로 삽입 시 인덱스 변화 방지
     */
    public List<GpxPoint> mergeGpxAndCheckpoints(List<TrackPoint> trackPoints, List<Checkpoint> checkpoints) {

       if (checkpoints.isEmpty()) {
           return buildFinalGpxPoints(trackPoints, null, null);
       }

        int[] insertPositions = findCheckpointInsertIndexArray(trackPoints, checkpoints);

        return buildFinalGpxPoints(trackPoints, checkpoints, insertPositions);
    }

    /**
     * 모든 체크포인트의 삽입 위치를 배열로 반환
     */
    private int[] findCheckpointInsertIndexArray(List<TrackPoint> trackPoints, List<Checkpoint> checkpoints) {
        int[] positions = new int[checkpoints.size()];

        for (int i = 0; i < checkpoints.size(); i++) {
            positions[i] = findClosestSegmentIndex(trackPoints, checkpoints.get(i));
        }

        return positions;
    }

    /**
     * 체크포인트에 가장 가까운 구간(선분) 찾기
     */
    private int findClosestSegmentIndex(List<TrackPoint> trackPoints, Checkpoint checkpoint) {
        double minDistance = Double.MAX_VALUE;
        int bestIndex = 1;

        for (int i = 0; i < trackPoints.size() - 1; i++) {
            TrackPoint start = trackPoints.get(i);
            TrackPoint end = trackPoints.get(i + 1);

            double distance = calculateDistanceToLineSegment(
                checkpoint.lat(), checkpoint.lon(),
                start.lat(), start.lon(),
                end.lat(), end.lon()
            );

            if (distance < minDistance) {
                minDistance = distance;
                bestIndex = i + 1;
            }
        }

        return bestIndex;
    }

    /**
     * 점에서 선분까지의 최단거리 계산
     */
    private double calculateDistanceToLineSegment(double px, double py,
                                                    double x1, double y1,
                                                    double x2, double y2) {
        // 1. 선분 벡터와 점 벡터 계산
        double segmentX = x2 - x1;
        double segmentY = y2 - y1;
        double pointX = px - x1;
        double pointY = py - y1;

        // 2. 선분 길이의 제곱 계산
        double segmentLengthSquared = segmentX * segmentX + segmentY * segmentY;

        // 3. 시작점과 끝점이 같은 경우 처리
        if (segmentLengthSquared == 0) {
            return Math.sqrt(pointX * pointX + pointY * pointY);
        }

        // 4. 투영 매개변수 t 계산 (0 ≤ t ≤ 1)
        double t = Math.max(0, Math.min(1,
            (pointX * segmentX + pointY * segmentY) / segmentLengthSquared));

        // 5. 선분 위의 가장 가까운 점 찾기
        double closestX = x1 + t * segmentX;
        double closestY = y1 + t * segmentY;
        double dx = px - closestX;
        double dy = py - closestY;

        // 6. 최단거리 반환
        return dx * dx + dy * dy;
    }

    /**
     * 최종 GpxPoint 리스트 생성
     */
    private List<GpxPoint> buildFinalGpxPoints(List<TrackPoint> trackPoints,
        List<Checkpoint> checkpoints,
        int[] insertPositions) {

        // 위치별 체크포인트 인덱스 매핑
        Map<Integer, List<Integer>> positionToCheckpoints = new HashMap<>();
        // null 체크 추가
        if (insertPositions != null && checkpoints != null) {
            for (int i = 0; i < insertPositions.length; i++) {
                positionToCheckpoints.computeIfAbsent(insertPositions[i], k -> new ArrayList<>()).add(i);
            }
        }

        List<GpxPoint> result = new ArrayList<>();
        double totalDistanceKm = 0.0;

        TrackPoint prev = trackPoints.get(0);
        result.add(GpxPoint.from(prev, 0.0, 0.0));

        for (int i = 1; i < trackPoints.size(); i++) {
            TrackPoint current = trackPoints.get(i);

            // 현재 위치의 체크포인트들 처리
            List<Integer> checkpointIndices = positionToCheckpoints.get(i);
            if (checkpointIndices != null) {
                for (int checkpointIndex : checkpointIndices) {
                    Checkpoint checkpoint = Objects.requireNonNull(checkpoints).get(checkpointIndex);

                    double distToCheckpoint = DistanceUtils.calculateDistance(
                        prev.lat(), prev.lon(),
                        checkpoint.lat(), checkpoint.lon()
                    );
                    totalDistanceKm += (distToCheckpoint / 1000.0);

                    result.add(new GpxPoint(
                        checkpoint.lat(),
                        checkpoint.lon(),
                        null,
                        distToCheckpoint,
                        totalDistanceKm
                    ));

                    prev = new TrackPoint(checkpoint.lat(), checkpoint.lon(), null);
                }
            }

            double segmentDistance = DistanceUtils.calculateDistance(
                prev.lat(), prev.lon(),
                current.lat(), current.lon()
            );
            totalDistanceKm += (segmentDistance / 1000.0);

            result.add(GpxPoint.from(current, segmentDistance, totalDistanceKm));
            prev = current;
        }

        return result;
    }

    // gpxTrackPoint -> List<GpxPoint> 변환 메서드
    private List<GpxPoint> convertGpxToGpxPoints(List<TrackPoint> trackPoints) {
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

    public List<CheckpointInfo> extractRandomCheckpoints(String gpxContent) {
        Gpx gpx = getGpxFromContent(gpxContent);
        List<TrackPoint> trackPoints = validGpx(gpx);

        if (trackPoints.size() < 8) {
            throw new CustomException(GPX_NOT_ENOUGH_POINTS);
        }

        List<TrackPoint> eligiblePoints = trackPoints.subList(1, trackPoints.size() - 1);
        int totalPoints = eligiblePoints.size();
        int interval = totalPoints / 7;

        if (interval < 1) {
            throw new CustomException(GPX_NOT_ENOUGH_POINTS);
        }

        List<TrackPoint> checkpointCandidates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int index = interval * i;
            if (index < totalPoints) {
                checkpointCandidates.add(eligiblePoints.get(index));
            }
        }

        Collections.shuffle(checkpointCandidates, new Random(System.nanoTime()));

        return checkpointCandidates.stream()
            .limit(7)
            .map(p -> new CheckpointInfo(
                UUID.randomUUID().toString(),
                p.lat(),
                p.lon()
            ))
            .toList();
    }
}
