package com.saisai.domain.ride.dto.response;

public record RideDeleteRes(
    Long deleteCount
) {

    public static RideDeleteRes of (long deleteCount) {
        return new RideDeleteRes(deleteCount);
    }
}
