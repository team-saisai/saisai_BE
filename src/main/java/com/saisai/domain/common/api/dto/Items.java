package com.saisai.domain.common.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Items<T>(
    @JsonProperty("item") List<T> item
    ) {
}
