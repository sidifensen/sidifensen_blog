package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleFavorite;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.entity.Favorite;
import com.sidifensen.domain.entity.Like;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.PrivateMessage;
import com.sidifensen.domain.entity.SysLoginLog;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.entity.SysUserRole;
import com.sidifensen.domain.entity.VipMember;
import com.sidifensen.domain.vo.ContentActivityVo;
import com.sidifensen.domain.vo.DashboardStatisticsVo;
import com.sidifensen.domain.vo.ExamineCountVo;
import com.sidifensen.domain.vo.InteractionTrendVo;
import com.sidifensen.domain.vo.UserDistributionVo;
import com.sidifensen.domain.vo.VipStatisticsVo;
import com.sidifensen.domain.vo.VisitorTrendVo;
import com.sidifensen.domain.vo.WeeklyTrendVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.ArticleFavoriteMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.CommentMapper;
import com.sidifensen.mapper.FavoriteMapper;
import com.sidifensen.mapper.LikeMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.PrivateMessageMapper;
import com.sidifensen.mapper.SysLoginLogMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.mapper.SysUserRoleMapper;
import com.sidifensen.mapper.VipMemberMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ArticleService;
import com.sidifensen.service.DashboardService;
import com.sidifensen.service.SysVisitorLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理端首页 Dashboard 统计服务实现
 *
 * @author sidifensen
 * @since 2026-03-15
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private PrivateMessageMapper privateMessageMapper;

    @Resource
    private ArticleService articleService;

    @Resource
    private SysVisitorLogService visitorLogService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private VipMemberMapper vipMemberMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private ArticleFavoriteMapper articleFavoriteMapper;

    @Override
    public DashboardStatisticsVo getDashboardStatistics() {
        return getDashboardStatistics(7);
    }

    @Override
    public DashboardStatisticsVo getDashboardStatistics(Integer trendDays) {
        // 1. 先尝试从 Redis 缓存获取
        Object cached = redisComponent.getDashboardStatistics();
        if (cached instanceof DashboardStatisticsVo) {
            DashboardStatisticsVo cachedVo = (DashboardStatisticsVo) cached;
            // 如果缓存中的趋势天数与请求的不一致，需要重新获取趋势数据
            if (cachedVo.getVisitorTrend() != null && cachedVo.getVisitorTrend().size() == trendDays) {
                return cachedVo;
            }
        }

        // 2. Redis 缓存未命中或趋势天数不匹配，从数据库查询并组装数据
        DashboardStatisticsVo result = new DashboardStatisticsVo();

        // 2.1 获取用户总数
        result.setUserTotalCount(getUserTotalCount());

        // 2.2 获取文章统计数据
        result.setArticleStatistics(articleService.getAdminStatistics());

        // 2.3 获取今日访问量和总访问量
        result.setTodayVisits(visitorLogService.getTodayVisitorCount());
        result.setTotalVisits(visitorLogService.getTotalVisitorCount());

        // 2.4 获取今日活跃用户数（当日登录的不同用户数）
        result.setTodayActiveUserCount(getTodayActiveUserCount());

        // 2.5 获取访客趋势数据
        result.setVisitorTrend(visitorLogService.getVisitorTrend(trendDays));

        // 3. 写入 Redis 缓存
        redisComponent.setDashboardStatistics(result);

        return result;
    }

    @Override
    public void refreshDashboardCache() {
        redisComponent.removeDashboardStatistics();
    }

    @Override
    public List<WeeklyTrendVo> getWeeklyTrend() {
        List<WeeklyTrendVo> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();

            // 统计当日新增文章数（已发布且创建时间在当日）
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
            articleWrapper.eq(Article::getExamineStatus, 1)
                          .ge(Article::getCreateTime, date.atStartOfDay())
                          .lt(Article::getCreateTime, date.plusDays(1).atStartOfDay());
            int articleCount = Math.toIntExact(articleMapper.selectCount(articleWrapper));

            // 统计当日新增用户数
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.ge(SysUser::getCreateTime, date.atStartOfDay())
                      .lt(SysUser::getCreateTime, date.plusDays(1).atStartOfDay());
            int userCount = Math.toIntExact(sysUserMapper.selectCount(userWrapper));

            WeeklyTrendVo vo = new WeeklyTrendVo()
                .setDate(dateStr)
                .setArticleCount(articleCount)
                .setUserCount(userCount);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<UserDistributionVo> getUserDistribution() {
        List<UserDistributionVo> result = new ArrayList<>();

        // 普通用户数（未绑定VIP角色且非管理员）
        // 管理员 role_id=1，VIP role_id=2
        Long totalUserCount = sysUserMapper.selectCount(null);
        Long adminCount = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
            .eq(SysUserRole::getRoleId, 1));
        Long vipCount = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
            .eq(SysUserRole::getRoleId, 2));

        result.add(new UserDistributionVo().setType("普通用户").setCount(totalUserCount - adminCount - vipCount));
        result.add(new UserDistributionVo().setType("VIP用户").setCount(vipCount));
        result.add(new UserDistributionVo().setType("管理员").setCount(adminCount));

        return result;
    }

    /**
     * 获取用户总数
     *
     * @return 用户总数
     */
    private Long getUserTotalCount() {
        try {
            return (long) sysUserMapper.selectCount(null);
        } catch (Exception e) {
            log.error("获取用户总数失败", e);
            throw new BlogException(BlogConstants.SystemInternalError);
        }
    }

    /**
     * 获取今日活跃用户数（当日登录的不同用户数）
     *
     * @return 今日活跃用户数
     */
    private Long getTodayActiveUserCount() {
        try {
            LocalDate today = LocalDate.now();
            // 查询今日登录且状态为成功的不同用户数
            LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysLoginLog::getStatus, 0)
                  .ge(SysLoginLog::getLoginTime, today.atStartOfDay())
                  .lt(SysLoginLog::getLoginTime, today.plusDays(1).atStartOfDay());
            // 统计不同 userId 的数量
            return sysLoginLogMapper.selectCount(wrapper);
        } catch (Exception e) {
            log.error("获取今日活跃用户数失败", e);
            return 0L;
        }
    }

    @Override
    public List<ContentActivityVo> getContentActivity() {
        List<ContentActivityVo> result = new ArrayList<>();

        // 获取各类数据总量或当日增量
        LocalDate today = LocalDate.now();

        // 文章活跃度：当日新增文章数
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.ge(Article::getCreateTime, today.atStartOfDay());
        long articleCount = articleMapper.selectCount(articleWrapper);

        // 评论活跃度：当日新增评论数
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.ge(Comment::getCreateTime, today.atStartOfDay());
        long commentCount = commentMapper.selectCount(commentWrapper);

        // 相册活跃度：当日新增相册数
        LambdaQueryWrapper<Album> albumWrapper = new LambdaQueryWrapper<>();
        albumWrapper.ge(Album::getCreateTime, today.atStartOfDay());
        long albumCount = albumMapper.selectCount(albumWrapper);

        // 收藏活跃度：当日新增收藏夹数
        LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.ge(Favorite::getCreateTime, today.atStartOfDay());
        long favoriteCount = favoriteMapper.selectCount(favoriteWrapper);

        // 私信活跃度：当日新增私信数
        LambdaQueryWrapper<PrivateMessage> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.ge(PrivateMessage::getCreateTime, today.atStartOfDay());
        long messageCount = privateMessageMapper.selectCount(messageWrapper);

        // 归一化到 0-100 分（取各类最大值作为基准）
        long maxCount = Math.max(articleCount, Math.max(commentCount,
            Math.max(albumCount, Math.max(favoriteCount, messageCount))));
        if (maxCount == 0) maxCount = 1; // 避免除零

        result.add(new ContentActivityVo().setItem("文章").setScore(normalize(articleCount, maxCount)));
        result.add(new ContentActivityVo().setItem("评论").setScore(normalize(commentCount, maxCount)));
        result.add(new ContentActivityVo().setItem("相册").setScore(normalize(albumCount, maxCount)));
        result.add(new ContentActivityVo().setItem("收藏").setScore(normalize(favoriteCount, maxCount)));
        result.add(new ContentActivityVo().setItem("私信").setScore(normalize(messageCount, maxCount)));

        return result;
    }

    /**
     * 归一化评分（0-100）
     *
     * @param count 当前数量
     * @param max 最大数量
     * @return 归一化评分
     */
    private int normalize(long count, long max) {
        return (int) Math.min(100, (count * 100) / max);
    }

    @Override
    public ExamineCountVo getExamineCount() {
        ExamineCountVo result = new ExamineCountVo();

        // 待审核文章数（examineStatus = 0）
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getExamineStatus, 0);
        result.setArticleCount(articleMapper.selectCount(articleWrapper));

        // 待审核评论数（examineStatus = 0）
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getExamineStatus, 0);
        result.setCommentCount(commentMapper.selectCount(commentWrapper));

        // 待审核图片数（examineStatus = 0）
        LambdaQueryWrapper<Photo> photoWrapper = new LambdaQueryWrapper<>();
        photoWrapper.eq(Photo::getExamineStatus, 0);
        result.setPhotoCount(photoMapper.selectCount(photoWrapper));

        return result;
    }

    @Override
    public VipStatisticsVo getVipStatistics() {
        VipStatisticsVo result = new VipStatisticsVo();

        // VIP 总数（status = active 的会员）
        LambdaQueryWrapper<VipMember> vipWrapper = new LambdaQueryWrapper<>();
        vipWrapper.eq(VipMember::getStatus, "active");
        Long totalVipCount = vipMemberMapper.selectCount(vipWrapper);
        result.setTotalVipCount(totalVipCount);

        // 近7天新增 VIP 数量
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        LambdaQueryWrapper<VipMember> newVipWrapper = new LambdaQueryWrapper<>();
        newVipWrapper.eq(VipMember::getStatus, "active")
                    .ge(VipMember::getCreateTime, sevenDaysAgo.atStartOfDay());
        result.setNewVipCount(vipMemberMapper.selectCount(newVipWrapper));

        // 近30天新增 VIP 数量
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LambdaQueryWrapper<VipMember> newVip30Wrapper = new LambdaQueryWrapper<>();
        newVip30Wrapper.eq(VipMember::getStatus, "active")
                      .ge(VipMember::getCreateTime, thirtyDaysAgo.atStartOfDay());
        result.setNewVipCount30Days(vipMemberMapper.selectCount(newVip30Wrapper));

        // 普通用户数 = 总用户数 - VIP用户数
        Long totalUserCount = sysUserMapper.selectCount(null);
        Long normalUserCount = totalUserCount - totalVipCount;
        result.setNormalUserCount(normalUserCount);

        // VIP占比百分比
        if (totalUserCount > 0) {
            double percentage = (totalVipCount * 100.0) / totalUserCount;
            result.setVipPercentage(Math.round(percentage * 10) / 10.0);
        } else {
            result.setVipPercentage(0.0);
        }

        return result;
    }

    @Override
    public List<VisitorTrendVo> getVisitorTrendByDays(Integer days) {
        return visitorLogService.getVisitorTrend(days);
    }

    @Override
    public List<InteractionTrendVo> getInteractionTrend() {
        List<InteractionTrendVo> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();

            // 统计当日评论数
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.ge(Comment::getCreateTime, date.atStartOfDay())
                         .lt(Comment::getCreateTime, date.plusDays(1).atStartOfDay());
            int commentCount = Math.toIntExact(commentMapper.selectCount(commentWrapper));

            // 统计当日文章点赞数（type = 0 表示文章点赞）
            LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.ge(Like::getCreateTime, date.atStartOfDay())
                      .lt(Like::getCreateTime, date.plusDays(1).atStartOfDay())
                      .eq(Like::getType, 0);
            int likeCount = Math.toIntExact(likeMapper.selectCount(likeWrapper));

            // 收藏数：当日新增文章收藏记录数
            LambdaQueryWrapper<ArticleFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
            favoriteWrapper.ge(ArticleFavorite::getCreateTime, date.atStartOfDay())
                         .lt(ArticleFavorite::getCreateTime, date.plusDays(1).atStartOfDay());
            int favoriteCount = Math.toIntExact(articleFavoriteMapper.selectCount(favoriteWrapper));

            InteractionTrendVo vo = new InteractionTrendVo()
                .setDate(dateStr)
                .setCommentCount(commentCount)
                .setLikeCount(likeCount)
                .setFavoriteCount(favoriteCount);
            result.add(vo);
        }

        return result;
    }

}
