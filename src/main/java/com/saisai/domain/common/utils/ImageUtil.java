package com.saisai.domain.common.utils;

import static com.saisai.domain.common.exception.ExceptionCode.SERVER_IMAGE_FAIL;

import com.saisai.domain.common.exception.CustomException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class ImageUtil {

    private final S3Client s3Client;
    private static final String BASE_DIRECTORY = "public/";

    @Value("${image.s3.bucket}")
    private String bucketName;

    @Value("${image.s3.endpoint}")
    private String endpoint;

    public String upload(MultipartFile imageFile, String directory) {
        if(imageFile == null) {
            return null;
        }

        String ext = "";
        int i = Objects.requireNonNull(imageFile.getOriginalFilename()).lastIndexOf('.');
        if (i > 0) {
            ext = imageFile.getOriginalFilename().substring(i);
        }
        String filename = UUID.randomUUID() + ext;

        String s3Key = directory + "/" + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(BASE_DIRECTORY + s3Key)
                .contentType(imageFile.getContentType())
                .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageFile.getBytes()));
            return s3Key;
        } catch (IOException e) {
            throw new CustomException(SERVER_IMAGE_FAIL);
        }
    }

    public void delete(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return;
        }
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(this.bucketName)
                .key(BASE_DIRECTORY + s3Key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(SERVER_IMAGE_FAIL);
        }
    }

    public String getImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        return this.endpoint + "/" + filename;
    }

}
