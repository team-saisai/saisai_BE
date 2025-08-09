package com.saisai.domain.checkpoint.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckpointItem(
    @JsonProperty("internal_id") String internalId,
    String title,
    @JsonProperty("latitude") Double lat,
    @JsonProperty("longitude") Double lon
) {

}
