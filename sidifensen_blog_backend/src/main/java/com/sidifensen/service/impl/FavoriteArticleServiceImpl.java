package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.FavoriteArticle;
import com.sidifensen.mapper.FavoriteArticleMapper;
import com.sidifensen.service.FavoriteArticleService;
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
public class FavoriteArticleServiceImpl extends ServiceImpl<FavoriteArticleMapper, FavoriteArticle> implements FavoriteArticleService {

}
