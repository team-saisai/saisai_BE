package com.saisai.domain.checkpoint.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // 파싱 안해도 될 필드가 있음
public record CheckpointRes(
    @JsonProperty("latitude") Double lat,
    @JsonProperty("longitude") Double lon
) {

}
