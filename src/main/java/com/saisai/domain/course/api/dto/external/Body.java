package com.saisai.domain.course.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Body<T> (
    @JsonProperty("items") DurunubiItems<T> durunubiItems,
    @JsonProperty("totalCount") Long totalCount,
    @JsonProperty("pageNo") Long pageNo,
    @JsonProperty("numOfRows") Long numOfRows
) {
}
