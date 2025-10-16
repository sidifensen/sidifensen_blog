package com.sidifensen.utils;

import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.exception.FileUploadException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Component
public class FileUploadUtils {

    @Resource
    private MinioClient client;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.publicPoint}")
    private String publicPoint;

    /**
     * 初始化方法，确保存储桶存在并设置为公开访问
     */
    @PostConstruct
    public void init() {
        try {
            // 检查存储桶是否存在，如果不存在则创建
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!bucketExists) {
                // 创建存储桶
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("成功创建 MinIO 存储桶: {}", bucketName);
            } else {
                log.info("MinIO 存储桶已存在: {}", bucketName);
            }

            // 设置桶的访问策略为公开读取
            setBucketPublicReadPolicy();

        } catch (Exception e) {
            log.error("初始化 MinIO 存储桶失败: {}", e.getMessage(), e);
            throw new FileUploadException("初始化存储桶失败");
        }
    }

    /**
     * 设置桶的访问策略为公开读取
     */
    private void setBucketPublicReadPolicy() {
        try {
            // MinIO 优化的公开读取策略 - 只允许读取对象，不允许列出桶内容
            String policy = String.format("""
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": [
                                    "s3:GetObject"
                                ],
                                "Resource": [
                                    "arn:aws:s3:::%s/*"
                                ]
                            }
                        ]
                    }
                    """, bucketName);

            // 设置桶策略
            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build());

            log.info("成功设置 MinIO 存储桶 {} 为公开读取访问（仅允许读取对象，不允许列出桶内容）", bucketName);
        } catch (Exception e) {
            log.error("设置 MinIO 存储桶公开访问策略失败: {}", e.getMessage(), e);
            // 这里不抛出异常，因为桶策略设置失败不应该影响应用启动
            // 但会记录错误日志，管理员可以手动设置
        }
    }

    /**
     * 上传文件
     *
     * @param uploadEnum 文件枚举
     * @param file       文件
     * @return 上传后的文件地址
     * @throws Exception 异常
     */
    public String upload(UploadEnum uploadEnum, MultipartFile file) {
        try {
            // 验证文件大小
            if (verifyTheFileSize(file.getSize(), uploadEnum.getLimitSize()))
                throw new FileUploadException("上传文件超过限制大小:" + uploadEnum.getLimitSize() + "MB");
            if (isFormatFile(file.getOriginalFilename(), uploadEnum.getFormat())) {
                InputStream stream = file.getInputStream();
                String name = UUID.randomUUID().toString().replace("-", "");
                String originalFilename = file.getOriginalFilename();
                String fileExtension = null;
                if (originalFilename != null) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                }
                PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .headers(Map.of("Content-Type", Objects.requireNonNull(file.getContentType())))
                        .object(uploadEnum.getDir() + name + "." + fileExtension)
                        .stream(stream, file.getSize(), -1)
                        .build();
                client.putObject(args);
                return publicPoint + "/" + bucketName + "/" + uploadEnum.getDir() + name + "." + fileExtension;
            }
            log.error("--------------------上传文件格式不正确--------------------");
            throw new FileUploadException("上传文件类型错误");
        } catch (FileUploadException e) {
            throw e; // 直接重新抛出特定的文件上传异常
        } catch (Exception e) {
            log.error("--------------------上传文件失败--------------------");
            log.error("上传文件失败: {}", e.getMessage(), e);
            // 检查是否是存储桶不存在的错误
            if (e.getMessage() != null && e.getMessage().contains("bucket does not exist")) {
                log.error("存储桶不存在，请检查 MinIO 配置和存储桶: {}", bucketName);
                throw new FileUploadException("存储桶不存在，请联系管理员");
            }
            throw new FileUploadException("上传文件失败");
        }
    }

    /**
     * 根据用户自定义的文件目录名上传文件到指定文件夹
     *
     * @param uploadEnum 文件枚举
     * @param file       文件
     * @param dirName    文件目录
     * @return 上传后的文件地址
     * @throws Exception 异常
     */
    public String upload(UploadEnum uploadEnum, MultipartFile file, String dirName) {
        try {
            // 验证文件大小
            if (verifyTheFileSize(file.getSize(), uploadEnum.getLimitSize()))
                throw new FileUploadException("上传文件超过限制大小:" + uploadEnum.getLimitSize() + "MB");

            if (isFormatFile(file.getOriginalFilename(), uploadEnum.getFormat())) {
                InputStream stream = file.getInputStream();
                // 生成随机文件名
                String name = UUID.randomUUID().toString().replace("-", "");
                String originalFilename = file.getOriginalFilename();
                String fileExtension = null;
                // 提取文件扩展名
                if (originalFilename != null) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                }

                // 文件夹目录
                String dir = uploadEnum.getDir() + dirName;

                PutObjectArgs args = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .headers(Map.of("Content-Type", Objects.requireNonNull(file.getContentType())))
                        .object(dir + "/" + name + "." + fileExtension)
                        .stream(stream, file.getSize(), -1)
                        .build();
                client.putObject(args);
                return publicPoint + "/" + bucketName + "/" + dir + "/" + name + "." + fileExtension;
            }
            log.error("--------------------上传文件格式不正确--------------------");
            throw new FileUploadException("上传文件类型错误");
        } catch (FileUploadException e) {
            throw e; // 直接重新抛出特定的文件上传异常
        } catch (Exception e) {
            log.error("--------------------上传文件失败--------------------");
            log.error("上传文件失败: {}", e.getMessage(), e);
            // 检查是否是存储桶不存在的错误
            if (e.getMessage() != null && e.getMessage().contains("bucket does not exist")) {
                log.error("存储桶不存在，请检查 MinIO 配置和存储桶: {}", bucketName);
                throw new FileUploadException("存储桶不存在，请联系管理员");
            }
            throw new FileUploadException("上传文件失败");
        }
    }

    public Boolean verifyTheFileSize(Long fileSize, Double limitSize) {
        // 转为相同大小格式
        double formatFileSize = convertFileSizeToMB(fileSize);
        if (formatFileSize < limitSize) {
            return false;
        }
        return true;
    }

    /**
     * B 转 MB
     *
     * @param sizeInBytes 文件大小 B
     * @return 文件大小 MB
     */
    public double convertFileSizeToMB(long sizeInBytes) {
        double sizeInMB = (double) sizeInBytes / (1024 * 1024);
        String formatted = String.format("%.2f", sizeInMB);
        // String转为Long
        return Double.parseDouble(formatted);
    }

    /**
     * 获取目录下的所有文件名称
     *
     * @param dir 目录
     * @return 所有文件全路径名称
     */
    public List<String> listFiles(String dir) {
        // 测试
        dir = dir.endsWith("/") ? dir : dir + "/";
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(dir)
                .build();
        Iterable<Result<Item>> results = client.listObjects(listObjectsArgs);

        List<String> fileNames = new ArrayList<>();
        results.forEach(result -> {
            Item item;
            try {
                // 提取出文件名
                item = result.get();
                fileNames.add(item.objectName());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                    | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                    | XmlParserException e) {
                log.error("获取文件出现错误", e);
            }
        });

        return fileNames;
    }

    /**
     * 批量删除
     *
     * @param fileNames 文件名称
     * @return 是否成功
     * @throws Exception 异常
     */
    public boolean deleteFiles(List<String> fileNames) {
        try {
            List<DeleteObject> deleteObjects = fileNames.stream().map(DeleteObject::new).toList();
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = client.removeObjects(removeObjectsArgs);
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("文件: " + error.objectName() + "删除错误; ", error.message());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new FileUploadException("删除文件失败");
        }
    }

    /**
     * 单文件删除
     *
     * @param fileName 文件名称
     * @param dir      文件目录
     * @return 是否成功, 成功：true, 失败：false
     */
    public boolean deleteFile(String dir, String fileName) {
        try {
            String objectName = dir + fileName; // 构建完整对象名
            if (!isFileExist(dir, fileName)) {
                log.error("文件 {} 不存在", objectName);
                throw new FileUploadException("文件不存在");
            }
            // 执行删除操作
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());

            log.info("文件 {} 已成功从 MinIO 中删除", objectName);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new FileUploadException("删除文件失败");
        }
    }

    /**
     * 文件格式校验
     *
     * @param fileName 文件名称
     * @param format   支持的后辍
     * @return 是否支持
     */
    public boolean isFormatFile(String fileName, List<String> format) {
        for (String s : format) {
            if (fileName.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件是否存在
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 是否存在，存在：true，不存在：false
     */
    public boolean isFileExist(String dir, String fileName) {
        dir = dir.endsWith("/") ? dir : dir + "/";
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(dir)
                .build();
        Iterable<Result<Item>> results = client.listObjects(listObjectsArgs);

        for (Result<Item> result : results) {
            Item item = null;
            try {
                item = result.get();
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                    | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                    | XmlParserException e) {
                log.error("判断文件是否存在出现错误,{}, 文件名：{}, 目录：{}", e.getMessage(), fileName, dir);
                throw new FileUploadException("文件不存在");
            }
            if (item != null && item.objectName().equals(dir + fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 完整路径中截取文件名
     *
     * @param path 完整路径
     * @return 文件名
     */
    public String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
