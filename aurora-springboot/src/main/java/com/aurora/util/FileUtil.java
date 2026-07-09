package com.aurora.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Objects;

@Slf4j
public class FileUtil {

    private static final String MD5_ALGORITHM = "md5";

    private static final int BUFFER_SIZE = 8192;

    public static String getMd5(InputStream inputStream) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            log.error("计算文件 MD5 失败", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭文件输入流失败", e);
            }
        }
    }

    public static String getExtName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = Objects.requireNonNull(originalFilename).split("\\.");
            if (filename.length < 2) {
                return null;
            }
            file = File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            log.error("MultipartFile 转临时文件失败", e);
        }
        return file;
    }

}
