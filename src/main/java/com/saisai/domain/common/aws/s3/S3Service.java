package com.saisai.domain.common.aws.s3;

import static com.saisai.domain.common.exception.ExceptionCode.S3_FILE_NOT_FOUND;
import static com.saisai.domain.common.exception.ExceptionCode.S3_SERVER_DOWNLOAD_FAIL;
import static com.saisai.domain.common.exception.ExceptionCode.S3_SERVER_UPLOAD_FAIL;

import com.saisai.domain.common.exception.CustomException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Service  {

    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucketName;

    @Value("${s3.base-path}")
    private String basePath;

    @Value("${s3.endpoint}")
    private String endpoint;

    // 업로드한 파일 저장
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null) {
            return null;
        }

        String ext = "";
        int i = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf('.');
        if (i > 0) {
            ext = file.getOriginalFilename().substring(i);
        }
        String filename = UUID.randomUUID() + ext;

        String s3Key = directory + "/" + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(this.basePath + s3Key)
                .contentType(file.getContentType())
                .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return s3Key;
        } catch (IOException e) {
            throw new CustomException(S3_SERVER_UPLOAD_FAIL);
        }
    }

    // url에서 다운로드 한 내용 저장
    public String uploadContent(String content, String directory, String filename,
        String contentType) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        String s3Key = directory + "/" + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(this.basePath + s3Key)
                .contentType(contentType)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
            return s3Key;
        } catch (Exception e) {
            throw new CustomException(S3_SERVER_UPLOAD_FAIL);
        }
    }

    // 파일 삭제
    public void delete(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return;
        }
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(this.bucketName)
                .key(this.basePath + s3Key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(S3_SERVER_UPLOAD_FAIL);
        }
    }

    // 파일 주소 가져오기
    public String getFileUrl(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return null;
        }
        return this.endpoint + "/" + s3Key;
    }

    // 파일 내용 가져오기
    public String getFileContent(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return null;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.bucketName)
                .key(this.basePath + s3Key)
                .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(getObjectRequest);
            return response.asUtf8String();
        } catch (NoSuchKeyException e) {
            throw new CustomException(S3_FILE_NOT_FOUND);
        } catch (Exception e) {
            throw new CustomException(S3_SERVER_DOWNLOAD_FAIL);
        }
    }
}
