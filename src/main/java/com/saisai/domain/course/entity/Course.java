package com.saisai.domain.course.entity;

import com.saisai.domain.common.BaseEntity;
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

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "contents", nullable = false, length = 255)
    private String contents;

    @Column(name = "summary", nullable = false, length = 255)
    private String summary;

    @Column(name = "level", nullable = false, length = 1)
    private String level;

    @Column(name = "distance", nullable = false)
    private Double distance; // 단위: Km

    @Column(name = "estimated_time", nullable = false)
    private Double estimatedTime; // 단위: 시간 (예: 1.5 시간)

    @Column(name = "sigun", nullable = false, length = 20) // 행정구역 (예: 경남 밀양시)
    private String sigun;

    @Column(name = "tour_info", length = 255) // 관광 정보
    private String tourInfo;

    @Column(name = "traveler_info", length = 255) // 여행자 정보
    private String travelerInfo;

    @Column(name = "gpx_path", nullable = false, length = 255) // GPX 파일 경로 (URL)
    private String gpxPath;


    @Builder
    public Course(String name, String contents, String summary, String level, Double distance,
        Double estimatedTime, String sigun, String tourInfo, String travelerInfo, String gpxPath) {
        this.name = name;
        this.contents = contents;
        this.summary = summary;
        this.level = level;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.sigun = sigun;
        this.tourInfo = tourInfo;
        this.travelerInfo = travelerInfo;
        this.gpxPath = gpxPath;
    }
}