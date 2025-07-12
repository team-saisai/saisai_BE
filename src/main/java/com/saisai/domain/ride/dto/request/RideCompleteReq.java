package com.saisai.domain.ride.dto.request;

import com.saisai.domain.common.annotation.FileSize;
import com.saisai.domain.common.annotation.FileType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record RideCompleteReq(
    @Min(value = 1, message = "소요시간은 1초 이상이어야 합니다.")
    @NotNull(message = "소요시간(초)은 필수 입력값입니다.")
    Long duration,

    @DecimalMin(value = "0.1", message = "완주 거리는 0.1km 이상이어야 합니다.")
    @NotNull(message = "완주 거리(km)는 필수 입력값입니다.")
    Double actualDistance,

    @FileSize
    @FileType(allowed = {"jpg", "jpeg", "png"}, message = "jpg, jpeg, png 형식만 업로드 가능합니다.")
    @NotNull(message = "코스 완주 사진은 필수 입력값입니다.")
    MultipartFile completedImage
) {
}
