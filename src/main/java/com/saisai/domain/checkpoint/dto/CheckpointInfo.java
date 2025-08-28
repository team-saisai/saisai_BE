package com.saisai.domain.checkpoint.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckpointInfo(
    String internalId,
    Double latitude,
    Double longitude
) {

}
