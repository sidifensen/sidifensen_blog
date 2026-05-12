package com.sidifensen.service.impl;

import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.utils.UserUtils;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private AlbumPhotoMapper albumPhotoMapper;

    @Spy
    private AlbumServiceImpl albumService;

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
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Album.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SysUser.class);

        albumService = spy(new AlbumServiceImpl());
        setField(albumService, "baseMapper", albumMapper);
        setField(albumService, "albumMapper", albumMapper);
        setField(albumService, "photoMapper", photoMapper);
        setField(albumService, "sysUserMapper", sysUserMapper);
        setField(albumService, "albumPhotoMapper", albumPhotoMapper);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    @DisplayName("非 owner 访问私有相册时应返回相册不存在")
    void getAlbum_PrivateAlbumForNonOwner_ThrowsNotFoundAlbum() {
        Album album = new Album()
                .setId(1)
                .setUserId(200)
                .setName("private")
                .setShowStatus(ShowStatusEnum.PRIVATE.getCode());
        when(albumMapper.selectById(1)).thenReturn(album);
        when(sysUserMapper.selectById(200)).thenReturn(new SysUser().setId(200).setNickname("other"));

        BlogException exception = assertThrows(BlogException.class, () -> albumService.getAlbum(1));

        assertEquals(BlogConstants.NotFoundAlbum, exception.getMessage());
        verify(albumPhotoMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("owner 可以读取自己的私有相册")
    void getAlbum_PrivateAlbumForOwner_ReturnsAlbum() {
        Album album = new Album()
                .setId(1)
                .setUserId(100)
                .setName("private")
                .setShowStatus(ShowStatusEnum.PRIVATE.getCode());
        when(albumMapper.selectById(1)).thenReturn(album);
        when(albumPhotoMapper.selectList(any())).thenReturn(List.of(new AlbumPhoto(1, 1, 10)));
        when(photoMapper.selectList(any())).thenReturn(List.of(
                new Photo().setId(10).setExamineStatus(ExamineStatusEnum.WAIT.getCode()).setUrl("owner-photo")
        ));
        when(sysUserMapper.selectById(100)).thenReturn(new SysUser().setId(100).setNickname("owner"));

        AlbumVo result = albumService.getAlbum(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("owner", result.getUserName());
        assertEquals(1, result.getPhotos().size());
    }

    @Test
    @DisplayName("公开搜索必须追加公开状态过滤")
    void searchAlbum_PublicSearch_ShouldFilterPublicStatus() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Album>> captor =
                ArgumentCaptor.forClass(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper.class);
        Album publicAlbum = new Album()
                .setId(1)
                .setUserId(100)
                .setName("public")
                .setShowStatus(ShowStatusEnum.PUBLIC.getCode());
        doReturn(List.of(publicAlbum)).when(albumService).list(captor.capture());
        try (MockedStatic<UserUtils> userUtilsMock = mockStatic(UserUtils.class)) {
            userUtilsMock.when(() -> UserUtils.getUserMap(any(), any()))
                    .thenReturn(Map.of(100, new SysUser().setId(100).setNickname("owner")));

            List<AlbumVo> result = albumService.searchAlbum("pub");

            assertEquals(1, result.size());
            String sqlSegment = captor.getValue().getSqlSegment();
            assertNotNull(sqlSegment);
            assertEquals(true, sqlSegment.contains("show_status"));
        }
    }
}
