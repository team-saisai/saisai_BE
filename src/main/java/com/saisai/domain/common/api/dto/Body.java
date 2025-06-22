package com.saisai.domain.common.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Body<T> (
    @JsonProperty("items") Items<T> items,
    @JsonProperty("totalCount") Long totalCount,
    @JsonProperty("pageNo") Long pageNo,
    @JsonProperty("numOfRows") Long numOfRows
) {
}
