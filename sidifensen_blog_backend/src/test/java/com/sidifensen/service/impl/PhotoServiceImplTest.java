package com.sidifensen.service.impl;

import com.sidifensen.config.SidifensenConfig;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.FileUploadUtils;
import com.sidifensen.utils.ImageAuditUtils;
import com.sidifensen.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PhotoServiceImplTest {

    @Mock
    private FileUploadUtils fileUploadUtils;

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private ImageAuditUtils imageAuditUtils;

    @Mock
    private AlbumPhotoMapper albumPhotoMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private MessageService messageService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SidifensenConfig sidifensenConfig;

    @Spy
    private PhotoServiceImpl photoService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private void setField(Object target, String name, Object value) throws Exception {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
        }
        Class<?> superClass = target.getClass().getSuperclass();
        while (superClass != null) {
            for (Field field : superClass.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                }
            }
            superClass = superClass.getSuperclass();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(100);

        photoService = spy(new PhotoServiceImpl());
        setField(photoService, "fileUploadUtils", fileUploadUtils);
        setField(photoService, "photoMapper", photoMapper);
        setField(photoService, "albumMapper", albumMapper);
        setField(photoService, "imageAuditUtils", imageAuditUtils);
        setField(photoService, "albumPhotoMapper", albumPhotoMapper);
        setField(photoService, "sysUserMapper", sysUserMapper);
        setField(photoService, "messageService", messageService);
        setField(photoService, "rabbitTemplate", rabbitTemplate);
        setField(photoService, "sidifensenConfig", sidifensenConfig);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    @DisplayName("不能向他人相册上传图片")
    void uploadAlbum_NotOwner_ThrowsExceptionBeforeUpload() {
        when(albumMapper.selectById(2)).thenReturn(new Album().setId(2).setUserId(200));
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2, 3});

        BlogException exception = assertThrows(BlogException.class, () -> photoService.uploadAlbum(file, 2));

        assertEquals(BlogConstants.CannotHandleOthersAlbum, exception.getMessage());
        verify(fileUploadUtils, never()).upload(any(), any());
    }
}
