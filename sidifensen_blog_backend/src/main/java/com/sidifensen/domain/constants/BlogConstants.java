package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
public class BlogConstants {

    public static final String NotFoundUser = "该用户不存在";
    public static final String ExistUser = "该用户已存在";
    public static final String UserDisabled = "账号被禁用";
    public static final String ExistUserName = "用户名已存在";
    public static final String ExistEmail = "邮箱已存在";
    public static final String NotFoundEmail = "邮箱不存在";
    public static final String CheckCodeError = "验证码错误";
    public static final String LoginRequired = "请先登录";
    public static final String LoginExpired = "登录已过期，请重新登录";
    public static final String NewPasswordSameAsOld = "新密码不能与原密码相同";
    public static final String UserIdRequired = "用户ID不能为空";
    public static final String UpdateUserInfoError = "更新用户信息失败";

    public static final String CannotGetRequestInfo = "无法获取请求信息";
    public static final String IllegalRequest = "非法请求";

    public static final String NotFoundRole = "角色不存在";
    public static final String NotFoundMenu = "菜单不存在";
    public static final String NotFoundPermission = "权限不存在";
    public static final String NotAdminAccount = "不是管理员账户";
    public static final String ExistRoleMenu = "角色菜单已存在";
    public static final String ExistRolePermission = "角色权限已存在";
    public static final String ExistUserRole = "用户角色已存在";

    public static final String NotFoundAlbum = "相册不存在";
    public static final String CannotHandleOthersAlbum = "不能操作别人的相册";
    public static final String CannotHandleOthersPhoto = "不能操作别人的照片";
    public static final String NotFoundPhoto = "照片不存在";
    public static final String ExamineStatusError = "审核状态错误";

    public static final String SaveMessageError = "消息保存失败";
    public static final String ReadMessageError = "消息读取失败";
    public static final String DeleteMessageError = "消息删除失败";

    public static final String AddTagError = "添加标签失败";
    public static final String DeleteTagError = "删除标签失败";
    public static final String AddArticleError = "添加文章失败";
    public static final String DeleteArticleError = "删除文章失败";
    public static final String CannotHandleOthersArticle = "不能操作别人的文章";
    public static final String UpdateArticleError = "更新文章失败";
    public static final String NotFoundArticle = "文章不存在";
    public static final String NotFoundTag = "标签不存在";

    public static final String NotFoundColumn = "专栏不存在";
    public static final String CannotHandleOthersColumn = "不能操作别人的专栏";
    public static final String AddColumnError = "添加专栏失败";
    public static final String UpdateColumnError = "更新专栏失败";
    public static final String DeleteColumnError = "删除专栏失败";
    public static final String ExamineColumnError = "审核专栏失败";
    public static final String BatchExamineColumnError = "批量审核专栏失败";
    public static final String BatchDeleteColumnError = "批量删除专栏失败";
    public static final String ColumnIdsEmpty = "专栏ID列表不能为空";
    public static final String ColumnIdEmpty = "专栏ID不能为空";
    public static final String ArticleNotInColumn = "文章不在该专栏中";
    public static final String RemoveArticleFromColumnError = "从专栏中移除文章失败";

    // 评论相关错误信息
    public static final String NotFoundComment = "评论不存在";
    public static final String NotFoundParentComment = "父评论不存在";
    public static final String CannotReplyUnApprovedComment = "无法回复未审核通过的评论";
    public static final String AddCommentError = "评论发表失败";
    public static final String CannotDeleteOthersComment = "无权限删除此评论";
    public static final String DeleteCommentError = "评论删除失败";
    public static final String ArticleIdRequired = "文章ID不能为空";
    public static final String CommentIdRequired = "评论ID不能为空";
    public static final String CommentExamineStatusError = "审核状态错误";
    public static final String CommentAuditError = "审核失败";
    public static final String CommentDeleteError = "评论删除失败";
    public static final String UpdateArticleCommentCountError = "更新文章评论数失败";

    // 点赞相关错误信息
    public static final String LikeTypeError = "点赞类型错误";
    public static final String UpdateArticleLikeCountError = "更新文章点赞数失败";

    // 阅读记录相关错误信息
    public static final String UpdateArticleReadCountError = "更新文章阅读数失败";
    public static final String SaveReadRecordError = "保存阅读记录失败";
    public static final String GetUserHistoryError = "获取用户浏览历史失败";
    public static final String ClearUserHistoryError = "清除用户浏览历史失败";

    // 收藏夹相关错误信息
    public static final String NotFoundFavorite = "收藏夹不存在";
    public static final String CannotHandleOthersFavorite = "不能操作别人的收藏夹";
    public static final String CannotAccessPrivateFavorite = "无权限访问私密收藏夹";
    public static final String AddFavoriteError = "添加收藏夹失败";
    public static final String UpdateFavoriteError = "更新收藏夹失败";
    public static final String FavoriteNameRequired = "收藏夹名称不能为空";
    public static final String AddArticleToFavoriteError = "添加文章到收藏夹失败";
    public static final String FavoriteIdRequired = "收藏夹ID不能为空";
    public static final String ArticleAlreadyInFavorite = "文章已在该收藏夹中";
    public static final String UpdateFavoriteArticleCountError = "更新收藏夹文章数失败";
    public static final String ArticleNotInFavorite = "文章不在该收藏夹中";
    public static final String RemoveArticleFromFavoriteError = "从收藏夹中移除文章失败";

    // 关注相关错误信息
    public static final String CannotFollowSelf = "不能关注自己";
    public static final String FollowError = "关注失败";
    public static final String UnfollowError = "取消关注失败";
    public static final String FollowedUserIdRequired = "被关注用户ID不能为空";

    // 友链相关错误信息
    public static final String NotFoundLink = "友链不存在";
    public static final String CannotHandleOthersLink = "不能操作别人的友链";
    public static final String AddLinkError = "申请友链失败";
    public static final String DeleteLinkError = "删除友链失败";
    public static final String LinkNameRequired = "网站名称不能为空";
    public static final String LinkUrlRequired = "网站地址不能为空";
    public static final String LinkDescriptionRequired = "网站描述不能为空";
    public static final String LinkEmailRequired = "网站邮箱不能为空";
    public static final String LinkIdRequired = "友链ID不能为空";
    public static final String GetLinkListError = "获取友链列表失败";
}
