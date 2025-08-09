package com.saisai.domain.checkpoint.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckpointResponse(
    boolean success,
    List<CheckpointItem> response
) {
}
