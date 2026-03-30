package com.sidifensen.domain.constants;

/**
 * RabbitMQ 常量类
 * 定义交换机、队列、路由键等常量
 * 
 * @author sidifensen
 * @since 2025-07-09
 */
public class RabbitMQConstants {

    // ==================== 邮件相关 ====================

    public static final String Email_Exchange = "email_exchange";

    public static final String Email_Queue = "email_queue";

    public static final String Email_Routing_Key = "email_routing_key";

    // 评论邮件通知路由键
    public static final String Comment_Email_Routing_Key = "comment_email_routing_key";

    // 系统邮件通知路由键
    public static final String System_Email_Routing_Key = "system_email_routing_key";

    // ==================== 审核相关 ====================
    
    public static final String Examine_Exchange = "examine_exchange";

    public static final String Examine_Queue = "examine_queue";

    public static final String Examine_Routing_Key = "examine_routing_key";

    // ==================== 友链审核相关 ====================
    
    public static final String Link_Approval_Exchange = "link_approval_exchange";

    public static final String Link_Approval_Queue = "link_approval_queue";

    public static final String Link_Approval_Routing_Key = "link_approval_routing_key";

    // ==================== 死信队列相关 ====================
    
    /**
     * 死信交换机
     * 用于接收重试失败的消息
     */
    public static final String Dead_Letter_Exchange = "dead_letter_exchange";

    /**
     * 死信队列
     * 存储重试失败的消息，需要人工介入处理
     */
    public static final String Dead_Letter_Queue = "dead_letter_queue";

    /**
     * 死信路由键
     */
    public static final String Dead_Letter_Routing_Key = "dead_letter_routing_key";

    // ==================== 访客记录相关 ====================
    
    /**
     * 访客记录交换机
     */
    public static final String Visitor_Exchange = "visitor_exchange";

    /**
     * 访客记录队列
     */
    public static final String Visitor_Queue = "visitor_queue";

    /**
     * 访客记录路由键
     */
    public static final String Visitor_Routing_Key = "visitor_routing_key";

    // ==================== 黑名单通知相关 ====================
    
    /**
     * 黑名单通知交换机
     */
    public static final String Blacklist_Exchange = "blacklist_exchange";

    /**
     * 黑名单通知队列
     */
    public static final String Blacklist_Queue = "blacklist_queue";

    /**
     * 黑名单通知路由键
     */
    public static final String Blacklist_Routing_Key = "blacklist_routing_key";

    // ==================== WebSocket 消息相关 ====================

    /**
     * WebSocket 消息交换机
     */
    public static final String WebSocket_Exchange = "websocket_exchange";

    /**
     * WebSocket 消息队列
     */
    public static final String WebSocket_Queue = "websocket_queue";

    /**
     * WebSocket 消息路由键前缀
     * 完整路由键格式：websocket.user.{userId}
     */
    public static final String WebSocket_Routing_Key_Prefix = "websocket.user.";

    // ==================== 操作日志相关 ====================

    /**
     * 操作日志交换机
     */
    public static final String Operationlog_Exchange = "operationlog_exchange";

    /**
     * 操作日志队列
     */
    public static final String Operationlog_Queue = "operationlog_queue";

    /**
     * 操作日志路由键
     */
    public static final String Operationlog_Routing_Key = "operationlog_routing_key";

    // ==================== 订单超时相关 ====================

    /**
     * 订单超时延迟交换机
     */
    public static final String Order_Expire_Exchange = "order_expire_exchange";

    /**
     * 订单超时延迟队列
     */
    public static final String Order_Expire_Queue = "order_expire_queue";

    /**
     * 订单超时路由键
     */
    public static final String Order_Expire_Routing_Key = "order_expire_routing_key";

    // ==================== 公告发送相关 ====================

    /**
     * 公告发送交换机
     */
    public static final String Announcement_Exchange = "announcement_exchange";

    /**
     * 公告发送队列
     */
    public static final String Announcement_Queue = "announcement_queue";

    /**
     * 公告发送路由键
     */
    public static final String Announcement_Routing_Key = "announcement_routing_key";

}
