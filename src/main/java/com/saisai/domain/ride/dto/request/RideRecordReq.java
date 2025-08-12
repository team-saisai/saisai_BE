package com.saisai.domain.ride.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RideRecordReq(
    @Min(value = 1, message = "소요시간은 1초 이상이어야 합니다.")
    @NotNull(message = "소요시간(초)은 필수 입력값입니다.")
    Long duration,

    @NotNull(message = "체크포인트 인덱스는 필수 입력값입니다.")
    Integer checkpointIdx
) {

}
