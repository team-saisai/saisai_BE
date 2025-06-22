package com.saisai.domain.common.utils;

import com.saisai.domain.common.api.dto.Body;
import com.saisai.domain.common.api.dto.ExternalResponse;
import com.saisai.domain.common.api.dto.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExternalResponseUtil {
    private ExternalResponseUtil() {}

    public static <T> List<T> extractItems(ExternalResponse<Body<T>> externalResponse) {
        return Optional.ofNullable(externalResponse)
            .map(ExternalResponse::response)
            .map(ExternalResponse.Response::body)
            .map(Body::items)
            .map(Items::item)
            .orElseGet(ArrayList::new);
    }

    public static <T> Long extractTotalCount(ExternalResponse<Body<T>> externalResponse) {
        return Optional.ofNullable(externalResponse)
            .map(ExternalResponse::response)
            .map(ExternalResponse.Response::body)
            .map(Body::totalCount)
            .orElse(0L);
    }

    public static <T> Long extractPageNo(ExternalResponse<Body<T>> externalResponse, int page) {
        return Optional.ofNullable(externalResponse)
            .map(ExternalResponse::response)
            .map(ExternalResponse.Response::body)
            .map(Body::pageNo)
            .orElse((long) page);
    }

    public static <T> Long extractNumOfRows(ExternalResponse<Body<T>> externalResponse) {
        return Optional.ofNullable(externalResponse)
            .map(ExternalResponse::response)
            .map(ExternalResponse.Response::body)
            .map(Body::numOfRows)
            .orElse(10L);
    }

}
