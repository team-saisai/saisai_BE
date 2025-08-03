package com.saisai.domain.course.dto.response;

public record BookmarksRemoveRes(
    Integer deleteCount
) {
    public static BookmarksRemoveRes of (int deleteCount) {
        return new BookmarksRemoveRes(deleteCount);
    }
}
