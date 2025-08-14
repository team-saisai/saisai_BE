package com.saisai.domain.user.dto.request;

import com.saisai.domain.common.annotation.FileSize;
import com.saisai.domain.common.annotation.FileType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUpdateReq(
    @FileSize
    @FileType(allowed = {"jpg", "jpeg", "png"}, message = "jpg, jpeg, png 형식만 업로드 가능합니다.")
    @NotNull(message = "이미지는 필수 입력값입니다.")
    MultipartFile image
) {

}
