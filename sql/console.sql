create table sys_user
(
    id               int primary key auto_increment comment '用户id',
    username         varchar(50)  not null comment '用户名',
    password         varchar(100) not null comment '密码',
    nickname         varchar(10)  not null comment '昵称',
    email            varchar(255) not null comment '邮箱',
    sex              tinyint               default 0 comment '性别 0-男 1-女',
    introduction     varchar(200) comment '简介',
    avatar           varchar(400) comment '头像',
    status           tinyint      not null default 0 comment '状态 0-正常 1-禁用',
    fans_count       int          not null default 0 comment '粉丝数',
    follow_count     int          not null default 0 comment '关注数',
    register_type    tinyint               default 0 comment '注册方式 0-用户名/邮箱 1-gitee 2-github 3-QQ',
    register_ip      varchar(100) comment '注册ip',
    register_address varchar(100) comment '注册地址',
    login_type       tinyint               default 0 comment '登录方式 0-用户名/邮箱 1-gitee 2-github 3-QQ',
    login_ip         varchar(100) comment '登录ip',
    login_address    varchar(100) comment '登录地址',
    login_time       datetime     not null comment '登录时间',
    create_time      datetime     not null comment '创建时间',
    update_time      datetime     not null comment '更新时间',
    is_deleted       tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_username (username),
    index idx_email (email)
);


create table sys_role
(
    id          int primary key auto_increment comment '角色id',
    role        varchar(10) not null comment '角色标识',
    name        varchar(10) not null comment '角色名称',
    description varchar(20) comment '角色描述',
    status      tinyint     not null default 0 comment '状态 0-正常 1-禁用',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table sys_user_role
(
    id      int primary key auto_increment comment '主键id',
    user_id int not null comment '用户id',
    role_id int not null comment '角色id',
    index user_id (user_id)
);

create table sys_menu
(
    id          int primary key auto_increment comment '菜单id',
    parent_id   int                  default 0 comment '父级id',
    name        varchar(10) not null comment '菜单名称',
    sort        int                  default 0 comment '排序',
    path        varchar(100) comment '路由路径',
    component   varchar(100) comment '组件路径',
    icon        varchar(30) comment '图标',
    status      tinyint     not null default 0 comment '状态 0-正常 1-禁用',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table sys_role_menu
(
    id      int primary key auto_increment comment '主键id',
    role_id int not null comment '角色id',
    menu_id int not null comment '菜单id',
    index idx_role_id (role_id)
);

create table sys_permission
(
    id          int primary key auto_increment comment '权限id',
    description varchar(20) not null comment '权限描述',
    permission  varchar(50) not null comment '权限',
    menu_id     int         not null comment '菜单id',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table sys_role_permission
(
    id            int primary key auto_increment comment '主键id',
    role_id       int not null comment '角色id',
    permission_id int not null comment '权限id',
    index idx_role_id (role_id)
);

create table photo
(
    id             int primary key auto_increment comment '图片id',
    user_id        bigint       not null comment '用户id',
    url            varchar(400) not null comment '图片url',
    examine_status tinyint      not null default 0 comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    create_time    datetime     not null comment '创建时间',
    update_time    datetime     not null comment '更新时间',
    is_deleted     tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_deleted_id_examine_status (is_deleted, examine_status)
);

create table album
(
    id          int primary key auto_increment comment '相册id',
    user_id     bigint      not null comment '用户id',
    name        varchar(10) not null comment '相册名称',
    cover_url   varchar(400) comment '相册封面',
    show_status tinyint     not null default 0 comment '展示状态 0-公开 1-私密',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_show_status (show_status),
    index idx_user_id (user_id)
);

create table album_photo
(
    id       int primary key auto_increment comment '主键id',
    album_id int not null comment '相册id',
    photo_id int not null comment '图片id',
    index idx_album_id (album_id)
);


create table message
(
    id          int primary key auto_increment comment '消息id',
    is_read     tinyint  not null default 0 comment '是否已读 0-未读 1-已读',
    type        tinyint  not null default 0 comment '消息类型 0-系统 1-评论 2-点赞 3-收藏 4-关注',
    content     text     not null comment '消息内容',
    sender_id   int      not null comment '发送消息的用户id',
    receiver_id int      not null comment '接收消息的用户id',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    is_deleted  tinyint  not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_sender_id_type (sender_id, type),
    index idx_receiver_id_type (receiver_id, type)
);

create table article
(
    id             int primary key auto_increment comment '文章id',
    user_id        int          not null comment '用户id',
    tag            varchar(255) not null comment '标签',
    title          varchar(50)  not null comment '标题',
    description    varchar(200) not null comment '描述',
    content        mediumtext   not null comment '内容',
    cover_url      varchar(400) comment '封面url',
    read_count     int          not null default 0 comment '阅读量',
    like_count     int          not null default 0 comment '点赞量',
    comment_count  int          not null default 0 comment '评论数',
    collect_count  int          not null default 0 comment '收藏量',
    examine_status tinyint      not null default 0 comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    edit_status    tinyint      not null default 0 comment '编辑状态 0-已发布 1-草稿箱 2-回收站',
    visible_range  tinyint      not null default 0 comment '可见范围 0-全部可见 1-仅我可见 2-粉丝可见 3-vip可见',
    reprint_type   tinyint      not null default 0 comment '转载类型 0-原创 1-转载',
    reprint_url    varchar(400) comment '转载链接',
    create_time    datetime     not null comment '创建时间',
    update_time    datetime     not null comment '更新时间',
    is_deleted     tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_user_id_examine_edit_visible_status_create_time (user_id, examine_status, edit_status, visible_range, create_time),
    index idx_examine_edit_visible_status_create_time (examine_status, edit_status, visible_range, create_time)
);

create table tag
(
    id       int primary key auto_increment comment '标签id',
    category varchar(10) not null comment '标签分类',
    name     varchar(15) not null comment '标签名称',
    unique index idx_name (name)
);

create table `column`
(
    id             int primary key auto_increment comment '专栏id',
    user_id        int         not null comment '用户id',
    sort           int         not null comment '排序',
    name           varchar(30) not null comment '专栏名称',
    description    varchar(200) comment '专栏描述',
    cover_url      varchar(400) comment '专栏封面',
    show_status    tinyint     not null default 0 comment '展示状态 0-公开 1-私密',
    examine_status tinyint     not null default 0 comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    focus_count    int         not null default 0 comment '关注数',
    article_count  int         not null default 0 comment '文章数',
    create_time    datetime    not null comment '创建时间',
    update_time    datetime    not null comment '更新时间',
    is_deleted     tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_user_id_examine_status (user_id, examine_status),
);

create table article_column
(
    id          int primary key auto_increment comment '主键id',
    sort        int      not null comment '排序',
    article_id  int      not null comment '文章id',
    column_id   int      not null comment '专栏id',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    is_deleted  tinyint  not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_article_id (article_id),
    index idx_column_id (column_id)
);

create table `like`
(
    id          int primary key auto_increment comment '点赞id',
    user_id     int      not null comment '点赞用户id',
    type        tinyint  not null comment '点赞类型 0-文章 1-评论',
    type_id     int      not null comment '点赞类型id',
    create_time datetime not null comment '创建时间',
    index idx_user_type_id (user_id, type, type_id),
    index idx_type_id_create_time (type, type_id, create_time)
);

create table comment
(
    id             int primary key auto_increment comment '评论id',
    parent_id      int                   default 0 comment '父级评论id',
    article_id     int          not null comment '文章id',
    user_id        int          not null comment '评论用户id',
    reply_user_id  int                   default null comment '回复的用户id',
    content        varchar(255) not null comment '评论内容',
    examine_status tinyint      not null default 0 comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    like_count     int          not null default 0 comment '点赞数',
    reply_count    int          not null default 0 comment '回复数',
    create_time    datetime     not null comment '创建时间',
    is_deleted     tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_article_id_parent_id_examine_status_create_time (article_id, parent_id, examine_status, create_time desc)
);

create table favorite
(
    id            int primary key auto_increment comment '收藏夹id',
    user_id       int         not null comment '用户id',
    name          varchar(20) not null comment '收藏夹名称',
    show_status   tinyint     not null default 0 comment '展示状态 0-公开 1-私密',
    article_count int         not null default 0 comment '文章数量',
    create_time   datetime    not null comment '创建时间',
    is_deleted    tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_user_status_time (user_id, show_status, create_time desc)
);

create table article_favorite
(
    id          int primary key auto_increment comment '主键id',
    article_id  int      not null comment '被收藏的文章id',
    favorite_id int      not null comment '收藏夹id',
    create_time datetime not null comment '创建时间',
    index idx_article_time (article_id, create_time desc),
    index idx_favorite_time (favorite_id, create_time desc)
);

create table history
(
    id         int primary key auto_increment comment '历史id',
    article_id int      not null comment '文章id',
    user_id    int      not null comment '用户id',
    view_time  datetime not null comment '浏览时间',
    is_deleted tinyint  not null default 0 comment '是否删除 0-未删除 1-已删除',
    -- 核心业务查询索引：登录用户去重检查
    index idx_article_user (article_id, user_id),
    -- 用户查询索引：查询某用户的浏览历史
    index idx_user_view_time (user_id, view_time desc)
);

create table follow
(
    id          int primary key auto_increment comment '关注id',
    follower_id int      not null comment '关注者用户id',
    followed_id int      not null comment '被关注者用户id',
    create_time datetime not null comment '关注时间',
    -- 查询某用户的关注列表
    index idx_follower_time (follower_id, create_time desc),
    -- 查询某用户的粉丝列表
    index idx_followed_time (followed_id, create_time desc)
);

create table link
(
    id             int primary key auto_increment comment '友链id',
    user_id        int          not null comment '用户id',
    name           varchar(20)  not null comment '网站名称',
    url            varchar(100) not null comment '网站地址',
    cover_url      varchar(100) not null comment '网站封面',
    description    varchar(100)  not null comment '网站描述',
    examine_status tinyint      not null default 0 comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    email          varchar(255) not null comment '网站邮箱',
    create_time    datetime     not null comment '创建时间',
    is_deleted     tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table blacklist
(
    id          int primary key auto_increment comment '黑名单id',
    type        tinyint      not null comment '黑名单类型 0-用户 1-ip地址',
    user_id     int                   default null comment '用户id',
    ip          varchar(100)          default null comment 'ip地址',
    reason      varchar(200) not null comment '拉黑原因', 
    ban_time    datetime     not null comment '拉黑时间',
    expire_time datetime              default null comment '到期时间',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间',
    is_deleted  tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除',
    -- 查询某用户是否在黑名单(检查是否过期)
    index idx_user_expire (user_id, expire_time),
    -- 查询某IP是否在黑名单(检查是否过期)
    index idx_ip_expire (ip, expire_time),
    -- 按类型和拉黑时间查询黑名单列表
    index idx_type_ban_time (type, ban_time desc)
);