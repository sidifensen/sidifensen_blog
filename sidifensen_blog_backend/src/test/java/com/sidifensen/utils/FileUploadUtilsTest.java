package com.sidifensen.utils;

import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.exception.FileUploadException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileUploadUtilsTest {

    @Mock
    private MinioClient minioClient;

    @Spy
    private FileUploadUtils fileUploadUtils;

    private void setField(Object target, String name, Object value) throws Exception {
        Class<?> current = target.getClass();
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                }
            }
            current = current.getSuperclass();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        setField(fileUploadUtils, "client", minioClient);
        setField(fileUploadUtils, "bucketName", "test-bucket");
        setField(fileUploadUtils, "publicPoint", "http://localhost:9000");
        setField(fileUploadUtils, "minioAvailable", true);
    }

    @Test
    @DisplayName("伪装为图片扩展名的非法内容必须被拒绝")
    void upload_FakeImageContent_ThrowsFileUploadException() {
        MockMultipartFile fakeImage = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "not-a-real-png".getBytes()
        );

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> fileUploadUtils.upload(UploadEnum.USER_AVATAR, fakeImage));

        assertEquals("上传文件类型错误", exception.getMessage());
        verifyNoInteractions(minioClient);
    }

    @Test
    @DisplayName("合法图片上传时应写入归一化的安全 MIME")
    void upload_RealImageContent_UsesNormalizedMimeType() throws Exception {
        byte[] pngHeader = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00
        };
        MockMultipartFile pngFile = new MockMultipartFile(
                "file",
                "avatar.PNG",
                "text/plain",
                pngHeader
        );

        fileUploadUtils.upload(UploadEnum.USER_AVATAR, pngFile);

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
        String contentType = captor.getValue().headers().entries().stream()
                .filter(entry -> "Content-Type".equals(entry.getKey()))
                .map(java.util.Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        assertEquals("image/png", contentType);
    }
}
