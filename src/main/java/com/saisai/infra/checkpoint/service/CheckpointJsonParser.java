package com.saisai.infra.checkpoint.service;

import static com.saisai.domain.common.exception.ExceptionCode.JSON_DESERIALIZATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.JSON_UNKNOWN_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.infra.checkpoint.dto.response.Checkpoint;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckpointJsonParser {

    private static final TypeReference<List<Checkpoint>> CHECKPOINT_LIST_TYPE =
        new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public List<Checkpoint> deserialize(String jsonContent) {
        if (jsonContent == null || jsonContent.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonContent, CHECKPOINT_LIST_TYPE);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_DESERIALIZATION_FAILED, e);
        } catch (Exception e) {
            throw new CustomException(JSON_UNKNOWN_ERROR, e);
        }
    }
}
