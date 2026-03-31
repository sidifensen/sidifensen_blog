package com.sidifensen.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * XSS 过滤工具类
 * 用于防止跨站脚本攻击（XSS）
 *
 * @author sidifensen
 * @since 2026-04-01
 */
@Slf4j
public class XssUtils {

    /**
     * 富文本白名单 - 允许的 HTML 标签和属性
     * 适用于文章内容、评论等需要保留格式的场景
     */
    private static final Safelist RICH_TEXT_WHITELIST = Safelist.relaxed()
            // 允许的标签
            .addTags("span", "pre", "code", "blockquote", "hr")
            // 允许的 attributes
            .addAttributes("span", "class")
            .addAttributes("pre", "class")
            .addAttributes("code", "class")
            // 允许 rel 属性（用于 a 标签安全链接）
            .addAttributes("a", "href", "title", "rel")
            // 强制 a 标签的 href 以安全协议开头
            .addProtocols("a", "href", "ftp", "http", "https", "mailto")
            // 禁止 img 的 src 属性使用 data: 协议
            .removeProtocols("img", "src", "data");

    /**
     * 纯文本白名单 - 禁止所有 HTML 标签
     * 适用于用户名、标题等不允许任何 HTML 的场景
     */
    private static final Safelist PLAIN_TEXT_WHITELIST = Safelist.none();

    /**
     * 过滤富文本内容
     * 保留基本格式（加粗、斜体、链接、图片等），移除危险脚本
     *
     * @param html HTML 富文本内容
     * @return 过滤后的安全 HTML
     */
    public static String cleanRichText(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        try {
            // 清理危险内容
            String cleanHtml = Jsoup.clean(html, "", RICH_TEXT_WHITELIST,
                    new Document.OutputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml));
            return cleanHtml;
        } catch (Exception e) {
            log.error("XSS 过滤异常，内容长度: {}，错误: {}", html.length(), e.getMessage());
            // 过滤异常时返回纯文本
            return Jsoup.parse(html).text();
        }
    }

    /**
     * 过滤纯文本内容
     * 移除所有 HTML 标签，只保留纯文本
     *
     * @param text 可能包含 HTML 的文本
     * @return 纯文本
     */
    public static String cleanPlainText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        try {
            String cleanText = Jsoup.clean(text, "", PLAIN_TEXT_WHITELIST);
            log.debug("纯文本 XSS 过滤完成，长度: {}", cleanText.length());
            return cleanText;
        } catch (Exception e) {
            log.error("纯文本 XSS 过滤异常: {}", e.getMessage());
            return text;
        }
    }

    /**
     * 过滤 HTML 属性中的 XSS
     * 适用于需要保留整个 HTML 结构，只过滤属性的场景
     *
     * @param html HTML 内容
     * @return 过滤后的 HTML
     */
    public static String cleanHtmlAttributes(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        try {
            return Jsoup.clean(html, "", Safelist.none()
                    .addAttributes(":all", "id", "class", "style")
                    .addProtocols("img", "src", "http", "https", "data"),
                    new Document.OutputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml));
        } catch (Exception e) {
            log.error("HTML 属性 XSS 过滤异常: {}", e.getMessage());
            return html;
        }
    }
}
