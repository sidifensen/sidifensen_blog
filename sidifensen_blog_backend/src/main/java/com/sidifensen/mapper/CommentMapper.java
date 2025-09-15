package com.sidifensen.mapper;

import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.vo.CommentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 *
 * @author sidifensen
 * @since 2025-09-15
 */
public interface CommentMapper extends BaseMapper<Comment> {

        /**
         * 查询文章的评论列表（包含用户信息）
         *
         * @param articleId     文章id
         * @param parentId      父级评论id（0表示顶级评论）
         * @param offset        偏移量
         * @param limit         限制数量
         * @param currentUserId 当前用户id（用于查询点赞状态）
         * @return 评论列表
         */
        List<CommentVo> selectCommentListWithUserInfo(@Param("articleId") Integer articleId,
                        @Param("parentId") Integer parentId,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit,
                        @Param("currentUserId") Integer currentUserId);

}
