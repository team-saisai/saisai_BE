package com.saisai.domain.badge.dto.request;

import com.saisai.domain.common.annotation.FileSize;
import com.saisai.domain.common.annotation.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record BadgeRegisterReq(
    @NotBlank(message = "뱃지명은 필수 입력값입니다.")
    @Size(max = 20, message = "뱃지명은 20자 이하로 입력해주세요.")
    String name,

    @NotBlank(message = "뱃지 설명은 필수 입력값입니다.")
    String description,

    @FileSize
    @FileType(allowed = {"jpg", "jpeg", "png"}, message = "jpg, jpeg, png 형식만 업로드 가능합니다.")
    @NotNull(message = "이미지는 필수 입력값입니다.")
    MultipartFile imageFile
) {

}
