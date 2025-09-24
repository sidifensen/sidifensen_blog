package com.sidifensen.domain.constants;

/**
 * MDC 日志上下文常量
 * @author sidifensen
 */
public class MDCConstants {
    
    // MDC 键名常量
    public static final String TRACE_ID = "traceId";           // 请求追踪ID
    public static final String USER_ID = "userId";             // 用户ID
    public static final String USERNAME = "username";          // 用户名
    public static final String REQUEST_URI = "requestUri";     // 请求URI
    public static final String REQUEST_METHOD = "requestMethod"; // 请求方法
    public static final String CLIENT_IP = "clientIp";         // 客户端IP
    public static final String SESSION_ID = "sessionId";       // 会话ID
    public static final String OPERATION_TYPE = "operationType"; // 操作类型
    public static final String MODULE_NAME = "moduleName";     // 模块名称
    public static final String BUSINESS_ID = "businessId";     // 业务ID（如文章ID、用户ID等）
    public static final String START_TIME = "startTime";       // 请求开始时间
    
    // 操作类型常量
    public static final String OP_QUERY = "QUERY";             // 查询操作
    public static final String OP_CREATE = "CREATE";           // 创建操作
    public static final String OP_UPDATE = "UPDATE";           // 更新操作
    public static final String OP_DELETE = "DELETE";           // 删除操作
    public static final String OP_LOGIN = "LOGIN";             // 登录操作
    public static final String OP_LOGOUT = "LOGOUT";           // 登出操作
    public static final String OP_AUDIT = "AUDIT";             // 审核操作
    
    // 模块名称常量
    public static final String MODULE_ARTICLE = "ARTICLE";     // 文章模块
    public static final String MODULE_COLUMN = "COLUMN";       // 专栏模块
    public static final String MODULE_COMMENT = "COMMENT";     // 评论模块
    public static final String MODULE_USER = "USER";           // 用户模块
    public static final String MODULE_AUTH = "AUTH";           // 认证模块
    public static final String MODULE_ADMIN = "ADMIN";         // 管理模块
}
