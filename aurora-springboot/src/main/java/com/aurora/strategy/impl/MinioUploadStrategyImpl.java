package com.aurora.strategy.impl;

import com.aurora.config.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service("minioUploadStrategyImpl")
public class MinioUploadStrategyImpl extends AbstractUploadStrategyImpl {

    private static final int UNKNOWN_PART_SIZE = -1;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public Boolean exists(String filePath) {
        boolean exist = true;
        try {
            getMinioClient()
                    .statObject(StatObjectArgs.builder().bucket(minioProperties.getBucketName()).object(filePath).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        try {
            getMinioClient().putObject(
                    PutObjectArgs.builder().bucket(minioProperties.getBucketName()).object(path + fileName).stream(
                                    inputStream, inputStream.available(), UNKNOWN_PART_SIZE)
                            .build());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return minioProperties.getUrl() + filePath;
    }

    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

}
