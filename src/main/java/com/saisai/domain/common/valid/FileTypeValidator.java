package com.saisai.domain.common.valid;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_FILE_NAME;
import static com.saisai.domain.common.exception.ExceptionCode.MISSING_FILE_EXTENSION;
import static com.saisai.domain.common.exception.ExceptionCode.UNSUPPORTED_FILE_TYPE;

import com.saisai.domain.common.annotation.FileType;
import com.saisai.domain.common.exception.CustomException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {

    private String[] allowed;

    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowed = constraintAnnotation.allowed();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile,
        ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }

        try {
            validateFileName(multipartFile.getOriginalFilename());
            validateFileType(multipartFile, this.allowed);
            return true;
        } catch (CustomException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage())
                .addConstraintViolation();
            return false;
        }
    }

    private void validateFileName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new CustomException(INVALID_FILE_NAME);
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            throw new CustomException(MISSING_FILE_EXTENSION);
        }
    }

    private void validateFileType(MultipartFile file, String[] allowed) {
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (!Arrays.asList(allowed).contains(extension)) {
            String allowedStr = String.join(", ", allowed);
            String errorMessage = String.format("%s 형식만 업로드 가능합니다.", allowedStr);
            throw new CustomException(UNSUPPORTED_FILE_TYPE, errorMessage);
        }
    }
}
