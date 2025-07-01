package com.saisai.domain.course.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.saisai.domain.common.utils.ParsingUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CourseItem(
    @JsonProperty("crsIdx") String courseId,
    @JsonProperty("crsKorNm") String courseName,
    @JsonProperty("crsLevel") String levelStr,
    @JsonProperty("crsDstnc") String distanceStr,
    @JsonProperty("crsTotlRqrmHour") String estimatedTimeStr,
    @JsonProperty("crsContents") String contents,
    @JsonProperty("crsSummary") String summary,
    @JsonProperty("crsTourInfo") String tourInfo,
    @JsonProperty("travelerinfo") String travelerinfo,
    @JsonProperty("sigun") String sigun,
    @JsonProperty("gpxpath") String gpxpath,
    @JsonProperty("crsIdx") String durunubiCourseId
) {
    public Integer level() {
        return ParsingUtils.safeParseInteger(this.levelStr);
    }

    public Double distance() {
        return ParsingUtils.safeParseDouble(this.distanceStr);
    }

    public Double estimatedTime() {
        return ParsingUtils.safeParseDouble(this.estimatedTimeStr);
    }
}
