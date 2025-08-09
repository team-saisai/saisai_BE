package com.saisai.domain.course.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DurunubiItems<T>(
    @JsonProperty("item") List<T> item
    ) {
}
