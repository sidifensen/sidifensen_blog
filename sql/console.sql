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
    title          varchar(100) not null comment '标题',
    description    varchar(200) not null comment '描述',
    content        text         not null comment '内容',
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

create table column
(
    id          int primary key auto_increment comment '专栏id',
    user_id     int         not null comment '用户id',
    name        varchar(10) not null comment '专栏名称',
    description varchar(200) comment '专栏描述',
    cover_url   varchar(400) comment '专栏封面',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除',
    unique index idx_name (name)
);

create table article_column
(
    id          int primary key auto_increment comment '主键id',
    article_id  int      not null comment '文章id',
    column_id   int      not null comment '专栏id',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    index idx_article_id (article_id),
    index idx_column_id (column_id)
);
