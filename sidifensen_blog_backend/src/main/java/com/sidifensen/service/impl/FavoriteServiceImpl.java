package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.Favorite;
import com.sidifensen.mapper.FavoriteMapper;
import com.sidifensen.service.FavoriteService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-16
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

}
