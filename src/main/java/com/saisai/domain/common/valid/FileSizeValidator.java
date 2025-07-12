package com.saisai.domain.common.valid;

import com.saisai.domain.common.annotation.FileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    private Long maxSizeInBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSizeInBytes = constraintAnnotation.maxSizeInMB() * 10224 * 1024;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile,
        ConstraintValidatorContext constraintValidatorContext) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }

        if (multipartFile.getSize() > maxSizeInBytes) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                String.format("파일 크기가 %dMB를 초과했습니다.", maxSizeInBytes / (1024 * 1024))
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
