package com.saisai.domain.common.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalResponse<T>(
    @JsonProperty("response") Response<T> response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response<T>(
        @JsonProperty("header") Object header,
        @JsonProperty("body") T body
    ) {}
}
