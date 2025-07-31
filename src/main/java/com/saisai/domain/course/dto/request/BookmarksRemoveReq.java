package com.saisai.domain.course.dto.request;

import java.util.Set;

public record BookmarksRemoveReq(
    Set<Long> courseIds
) {
}
