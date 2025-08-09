package com.saisai.domain.course.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DurunubiApiResponse<T>(
    @JsonProperty("response") Response<T> response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response<T>(
        @JsonProperty("header") Object header,
        @JsonProperty("body") T body
    ) {}
}
