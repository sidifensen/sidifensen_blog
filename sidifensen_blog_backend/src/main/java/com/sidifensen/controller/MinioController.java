package com.sidifensen.controller;
import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.utils.FileUploadUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
 
@RestController
public class MinioController {
 
    @Resource
    private MinioClient minioClient;

    @Resource
    private FileUploadUtils fileUploadUtils;
 
    @Value("${minio.bucket-name}")
    private String bucketName;

 
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileUploadUtils.upload(UploadEnum.USER_AVATAR,file);
            return Result.success("上传成功");
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return Result.error("上传失败: "+ e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
}