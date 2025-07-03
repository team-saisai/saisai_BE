package com.saisai.domain.course.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.gpx.dto.FirstGpxPoint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "level", nullable = false, length = 1)
    private Integer level;

    @Column(name = "distance", nullable = false)
    private Double distance; // 단위: Km

    @Column(name = "estimated_time", nullable = false)
    private Double estimatedTime; // 단위: 시간 (예: 1.5 시간)

    @Column(name = "sigun", nullable = false, length = 20) // 행정구역 (예: 경남 밀양시)
    private String sigun;

    @Column(name = "gpx_path", nullable = false, length = 255) // GPX 파일 경로 (URL)
    private String gpxPath;

    @Column(name = "durunubi_course_id") // 두루누비API 제공 코스 고유번호 (구분하기 위해 추가)
    private String durunubiCourseId;

    @Column(name = "image")
    private String image;

    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_lon")
    private Double startLon;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


    @Builder
    public Course(String name, String summary, Integer level, Double distance,
        Double estimatedTime, String sigun, String gpxPath,
        String durunubiCourseId, String image, Double startLat, Double startLon
    ) {
        this.name = name;
        this.summary = summary;
        this.level = level;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.sigun = sigun;
        this.gpxPath = gpxPath;
        this.durunubiCourseId = durunubiCourseId;
        this.image = image;
        this.startLat = startLat;
        this.startLon = startLon;
        this.isDeleted = false;
    }

    public static Course from (CourseItem courseItem, FirstGpxPoint firstGpxPoint, String gpxPath) {
        return Course.builder()
            .name(courseItem.courseName())
            .summary(courseItem.summary())
            .level(courseItem.level())
            .distance(courseItem.distance())
            .estimatedTime(courseItem.estimatedTime())
            .sigun(courseItem.sigun())
            .gpxPath(gpxPath)
            .durunubiCourseId(courseItem.durunubiCourseId())
            .startLat(firstGpxPoint.lat())
            .startLon(firstGpxPoint.lon())
            .build();
    }
}