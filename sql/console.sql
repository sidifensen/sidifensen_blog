create table sys_user
(
    id            int primary key auto_increment comment '用户id',
    username      varchar(10)  not null comment '用户名',
    password      varchar(100) not null comment '密码',
    nickname      varchar(10)  not null comment '昵称',
    email         varchar(50)  not null comment '邮箱',
    sex           tinyint               default 0 comment '性别 0-男 1-女',
    introduction  varchar(50) comment '简介',
    avatar        varchar(255) comment '头像',
    status        tinyint      not null default 0 comment '状态 0-正常 1-禁用',
    register_type tinyint               default 0 comment '注册方式 0-用户名/邮箱注册 1-第三方注册',
    register_ip   varchar(100) comment '注册ip',
    login_type    tinyint               default 0 comment '登录方式 0-用户名/邮箱登录 1-第三方登录',
    login_ip      varchar(100) comment '登录ip',
    login_time    datetime     not null comment '登录时间',
    create_time   datetime     not null comment '创建时间',
    update_time   datetime     not null comment '更新时间',
    is_deleted    tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除'
);


create table sys_role
(
    id          int primary key auto_increment comment '角色id',
    role        varchar(10) not null comment '角色',
    name        varchar(10) not null comment '角色名称',
    description varchar(20) comment '角色描述',
    sort        int                  default 0 comment '  排序',
    status      tinyint     not null default 0 comment '状态 0-正常 1-禁用',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table sys_user_role
(
    id      int primary key auto_increment comment '主键id',
    user_id int not null comment '用户id',
    role_id int not null comment '角色id'
);

create table sys_menu
(
    id             int primary key auto_increment comment '菜单id',
    parent_id      int                  default 0 comment '父级id',
    title          varchar(30) not null comment '菜单标题',
    sort           int                  default 0 comment '排序',
    type           tinyint              default 0 comment '类型 0-目录 1-菜单 2-按钮',
    path           varchar(100) comment '路径',
    component_path varchar(100) comment '组件路径',
    permission     varchar(100) comment '权限标识',
    icon           varchar(30) comment '图标',
    status         tinyint     not null default 0 comment '状态 0-正常 1-禁用',
    create_time    datetime    not null comment '创建时间',
    update_time    datetime    not null comment '更新时间',
    is_deleted     tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除'
);

create table sys_role_menu
(
    id      int primary key auto_increment comment '主键id',
    role_id int not null comment '角色id',
    menu_id int not null comment '菜单id'
);

# create table article
# (
#     id          bigint primary key auto_increment comment '文章id',
#     user_id     int          not null comment '用户id',
#     category_id int          not null comment '分类id',
#     tag_id      int          not null comment '标签id',
#     title       varchar(100) not null comment '标题',
#     content     text         not null comment '内容',
#     status      tinyint      not null default 0 comment '状态 0-正常 1-草稿 2-删除',
#     page_views  int          not null default 0 comment '访问量',
#     create_time datetime     not null comment '创建时间',
#     update_time datetime     not null comment '更新时间',
#     is_deleted  tinyint      not null default 0 comment '是否删除 0-未删除 1-已删除'
# );

INSERT INTO `sys_menu`
VALUES (1, 0, '首页', 0, 0, '/index', 'index', 'index', 'home', 0, '2025-06-28 22:31:52', '2025-06-28 22:30:22', 0);
INSERT INTO `sys_role`
VALUES (1, 'admin', '超级管理员', NULL, 0, 0, '2025-06-28 22:31:00', '2025-06-28 22:30:22', 0);
INSERT INTO `sys_role_menu`
VALUES (1, 1, 1);
INSERT INTO `sys_user`
VALUES (1, 'sidifensen', '$2a$10$XqlrDmPYn2z12dh2IIscm.TdbXvQXD2lVJEc08j5vLM.QP8NUG/P2', '斯蒂芬森', '123456@qq.com', 0,
        'helloworld', 'https://www.sidifensen.com/avatar.jpg', 0, 0, NULL, 0, '127.0.0.1', '2025-07-08 20:53:48',
        '2025-06-28 22:30:22', '2025-06-28 22:30:22', 0);
INSERT INTO `sys_user_role`
VALUES (1, 1, 1);

create table photo
(
    id             int primary key auto_increment comment '图片id',
    user_id        bigint            not null comment '用户id',
    url            varchar(255)      not null comment '图片url',
    album_id       bigint            not null comment '相册id',
    show_status    tinyint default 0 not null comment '展示状态 0-公开 1-私密',
    examine_status tinyint default 0 not null comment '审核状态 0-待审核 1-审核通过 2-审核未通过',
    create_time    datetime          not null comment '创建时间',
    update_time    datetime          not null comment '更新时间',
    is_deleted     tinyint           not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_examine_status (examine_status),
    index idx_photo_user_album_status_create_time (user_id, album_id, show_status, examine_status, create_time)
);

create table album
(
    id          int primary key auto_increment comment '相册id',
    user_id     bigint      not null comment '用户id',
    name        varchar(10) not null comment '相册名称',
    description varchar(20) comment '相册描述',
    cover_url   varchar(255) comment '相册封面',
    show_status tinyint              default 0 not null comment '展示状态 0-公开 1-私密',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    is_deleted  tinyint     not null default 0 comment '是否删除 0-未删除 1-已删除',
    index idx_show_status (show_status),
    index idx_user_status_create_time (user_id, show_status, create_time)
);

