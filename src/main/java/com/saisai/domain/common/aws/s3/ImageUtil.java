package com.saisai.domain.common.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageUtil {

    private final S3Service s3Service;
    private static final String IMAGE_DIRECTORY = "/image";

    // 이미지 업로드
    public String upload(MultipartFile imageFile, String directory) {
        return s3Service.uploadFile(imageFile, directory + IMAGE_DIRECTORY);
    }

    // 이미지 삭제
    public void delete(String s3Key) {
        s3Service.delete(s3Key);
    }

    // 이미지 URL 획득
    public String getImageUrl(String s3Key) {
        return s3Service.getFileUrl(s3Key);
    }

}
