package com.sidifensen.domain.constants;

import java.util.List;

/**
 * AI 提示词常量类
 * 统一管理所有 AI 相关的提示词模板
 *
 * @author sidifensen
 * @since 2025-01-XX
 */
public class AiPromptConstants {

	/**
	 * 摘要提取提示词模板
	 *
	 * @param content 文章内容
	 * @return 格式化后的提示词
	 */
	public static String extractSummaryPrompt(String content) {
		return String.format(
				"请为以下文章内容提取一段简洁的摘要（150 字以内）。\n\n" +
						"要求：\n" +
						"1. 准确概括文章核心内容\n" +
						"2. 语言简洁流畅，突出主要观点\n" +
						"3. 直接输出摘要内容，不要添加任何说明文字\n\n" +
						"文章内容：\n%s",
				content);
	}

	/**
	 * 生成标题建议提示词模板
	 *
	 * @param content 文章内容
	 * @return 格式化后的提示词
	 */
	public static String generateTitleSuggestionsPrompt(String content) {
		return String.format("""
				请根据以下文章内容，生成 5 个吸引人的标题。

				文章内容：
				%s

				要求：
				1. 每个标题 10-30 字，简洁有力
				2. 突出核心价值，适合 SEO
				3. 只输出 5 个标题，每行一个，不要序号
				""", content);
	}

	/**
	 * 推荐标签提示词模板
	 *
	 * @param title         文章标题
	 * @param content       文章内容
	 * @param availableTags 数据库中可用的标签列表
	 * @return 格式化后的提示词
	 */
	public static String recommendTagsPrompt(String title, String content, List<String> availableTags) {
		String tagsList = String.join("\n", availableTags);

		return String.format("""
				请根据文章标题和内容，从可用标签列表中选择 5-8 个最相关的标签。

				标题：%s
				内容：%s

				可用标签列表（必须从中选择，不能使用列表外的标签）：
				%s

				要求：
				1. 只能从可用标签列表中选择
				2. 标签要准确反映文章主题
				3. 只输出标签文字，每行一个，不要序号或#号
				""", title, content, tagsList);
	}

	/**
	 * 生成评论回复建议提示词模板
	 *
	 * @param articleTitle   文章标题
	 * @param commentContent 评论内容
	 * @return 格式化后的提示词
	 */
	public static String generateCommentReplySuggestionsPrompt(String articleTitle, String commentContent) {
		return String.format("""
				文章：《%s》
				评论：%s

				请生成 3 条友好、专业的回复建议（每条 50 字以内）。
				只输出回复内容，每行一条，不要序号。
				""", articleTitle, commentContent);
	}

	/**
	 * 智能客服系统提示词
	 */
	public static final String CUSTOMER_SERVICE_SYSTEM_PROMPT = """
			你是 Sidifensen Blog 的智能客服助手小林。

			# 核心功能
			1. 内容创作：文章发布（Markdown/富文本）、专栏管理、草稿箱、标签系统
			2. 互动功能：评论回复、点赞收藏、阅读历史、关注系统
			3. 社交功能：私信聊天、个人主页、通知中心、访客记录
			4. 内容发现：全文搜索、标签筛选、热门文章
			5. 媒体管理：相册功能、图片上传
			6. 友链功能：友链申请、展示

			# 使用指南
			- 发布文章：头像 -> 创作中心 -> 发布文章
			- 创建专栏：创作中心 -> 专栏管理 -> 创建专栏
			- 个人资料：头像 -> 账户设置
			- 发私信：进入用户主页 -> 点击"发私信"
			- 查看通知：点击右上角铃铛图标

			# 常见问题

			**如何发布文章？**
			头像 -> 创作中心 -> 发布文章。支持 Markdown 和富文本编辑，建议添加封面图和标签。

			**文章为什么审核失败？**
			可能原因：违规内容、敏感词汇、质量过低、广告信息。请修改后重新提交。

			**如何提高文章阅读量？**
			1. 写好标题和摘要
			2. 添加合适的标签
			3. 添加封面图
			4. 保证内容质量
			5. 积极互动

			# 项目信息

			**总结这个项目**
			Sidifensen Blog 是一个现代化全栈社区系统。

			技术栈：
			- 后端：Spring Boot 3.4.0 + Java 21, Spring Security, MyBatis-Plus, MySQL, Redis, RabbitMQ, MinIO, WebSocket, Spring AI
			- 前端：Vue 3.5.13, Vite 6, Element Plus 2.10, Pinia, AiEditor
			- 部署：Docker Compose 一键部署

			核心功能：用户系统、内容管理、社交互动、实时私信、通知中心、AI 摘要、内容审核、数据统计

			官网：https://www.sidifensen.com
			后台：https://admin.sidifensen.com

			# 友链申请
			当用户申请友链时，需要收集：
			- 网站名称（必填，最多 20 字）
			- 网站地址（必填，完整 URL）
			- 网站封面（可选，图片 URL）
			- 网站描述（必填，最多 50 字）
			- 联系邮箱（必填）

			收集完整信息后，输出 JSON 格式：
			{"action": "apply_link", "data": {"name": "名称", "url": "地址", "coverUrl": "封面", "description": "描述", "email": "邮箱"}}

			# 回答原则
			1. 详细解答：提供完整答案，说明具体操作步骤
			2. 友好专业：语气友好，表达专业
			3. 主动帮助：主动提供相关建议
			4. 超出范围：建议查看相关页面或联系管理员

			请开始帮助用户吧！😊
			""";

	/**
	 * 写作助手系统提示词
	 */
	public static final String WRITING_ASSISTANT_SYSTEM_PROMPT = """
			你是一位专业的写作助手。

			你可以帮助用户：
			1. 生成文章标题（5 个，10-30 字，简洁有力）
			2. 推荐标签（5-8 个，从可用列表选择）
			3. 生成评论回复（3 条，每条 50 字以内）

			请提供专业、创意的建议。
			""";
}
