package com.sidifensen.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.teaopenapi.Client;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teaopenapi.models.OpenApiRequest;
import com.aliyun.teaopenapi.models.Params;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.result.ImageAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字内容审核工具类
 *
 * @author sidifensen
 * @since 2025-08-29
 */
@Slf4j
@Component
public class TextAuditUtils {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.imageaudit.endpoint}")
    private String endpoint;

    // 常见的HTML标签列表，用于更精确地识别HTML标签
    private static final Set<String> COMMON_HTML_TAGS = new HashSet<>();

    static {
        String[] tags = {
                "html", "head", "title", "base", "link", "meta", "style",
                "script", "noscript", "body", "section", "nav", "article", "aside",
                "h1", "h2", "h3", "h4", "h5", "h6", "header", "footer", "address",
                "p", "hr", "pre", "blockquote", "ol", "ul", "li", "dl", "dt", "dd",
                "figure", "figcaption", "main", "div", "a", "em", "strong", "small",
                "s", "cite", "q", "dfn", "abbr", "ruby", "rt", "rp", "data", "time",
                "code", "var", "samp", "kbd", "sub", "sup", "i", "b", "u", "mark",
                "bdi", "bdo", "span", "br", "wbr", "ins", "del", "picture", "source",
                "img", "iframe", "embed", "object", "param", "video", "audio", "track",
                "canvas", "map", "area", "table", "caption", "colgroup", "col", "tbody",
                "thead", "tfoot", "tr", "td", "th", "form", "label", "input", "button",
                "select", "datalist", "optgroup", "option", "textarea", "keygen", "output",
                "progress", "meter", "fieldset", "legend", "details", "summary", "menuitem", "menu"
        };
        for (String tag : tags) {
            COMMON_HTML_TAGS.add(tag);
        }
    }

    /**
     * 去除HTML标签，只保留纯文本内容
     *
     * @param content 包含HTML标签的内容
     * @return 去除HTML标签后的纯文本内容
     */
    public String removeHtmlTags(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String result = content;

        // 使用正则表达式匹配HTML标签，并结合已知标签列表进行验证
        // 匹配开始标签和自闭合标签
        Pattern startTagPattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)(\\s[^>]*?|\\s*)/?>");
        Matcher startTagMatcher = startTagPattern.matcher(result);
        StringBuffer sb1 = new StringBuffer();

        while (startTagMatcher.find()) {
            String tagName = startTagMatcher.group(1).toLowerCase();

            // 只有在已知标签列表中的才替换为空
            if (COMMON_HTML_TAGS.contains(tagName)) {
                startTagMatcher.appendReplacement(sb1, "");
            } else {
                // 不是已知HTML标签，保留原内容
                startTagMatcher.appendReplacement(sb1, startTagMatcher.group(0));
            }
        }
        startTagMatcher.appendTail(sb1);
        result = sb1.toString();

        // 匹配结束标签
        Pattern endTagPattern = Pattern.compile("</([a-zA-Z][a-zA-Z0-9]*)\\s*>");
        Matcher endTagMatcher = endTagPattern.matcher(result);
        StringBuffer sb2 = new StringBuffer();

        while (endTagMatcher.find()) {
            String tagName = endTagMatcher.group(1).toLowerCase();

            // 只有在已知标签列表中的才替换为空
            if (COMMON_HTML_TAGS.contains(tagName)) {
                endTagMatcher.appendReplacement(sb2, "");
            } else {
                // 不是已知HTML标签，保留原内容
                endTagMatcher.appendReplacement(sb2, endTagMatcher.group(0));
            }
        }
        endTagMatcher.appendTail(sb2);
        result = sb2.toString();

        return result;
    }

    /**
     * 根据label获取对应的违规分类描述
     *
     * @param label 审核标签
     * @return 违规分类描述
     */
    private String getLabelDescription(String label) {
        switch (label) {
            case "spam":
                return "垃圾内容";
            case "politics":
                return "敏感内容";
            case "abuse":
                return "辱骂内容";
            case "terrorism":
                return "暴恐内容";
            case "porn":
                return "鉴黄内容";
            case "flood":
                return "灌水内容";
            case "contraband":
                return "违禁内容";
            case "ad":
                return "广告内容";
            default:
                return label;
        }
    }

    /**
     * 创建阿里云内容审核客户端
     *
     * @return Client
     * @throws Exception
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/imageaudit
        config.endpoint = endpoint;
        return new Client(config);
    }

    /**
     * 创建API信息
     *
     * @return Params
     */
    private Params createApiInfo() {
        return new Params()
                // 接口名称
                .setAction("ScanText")
                // 接口版本
                .setVersion("2019-12-30")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("formData")
                // 接口响应体内容格式
                .setBodyType("json");
    }

    /**
     * 审核文字内容并返回详细结果
     *
     * @param textContent 文字内容
     * @return 审核结果详情
     */
    public ImageAuditResult auditTextWithDetails(String textContent) {
        // 去除HTML标签，只保留纯文本内容进行审核
        String cleanTextContent = removeHtmlTags(textContent);

        try {
            Client client = createClient();
            Params params = createApiInfo();

            // 构造请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("Tasks.1.Content", cleanTextContent);
            // 检测场景
            map.put("Labels.1.Label", "spam"); // 垃圾内容识别
            map.put("Labels.2.Label", "politics"); // 敏感内容识别
            map.put("Labels.3.Label", "abuse"); // 辱骂内容识别
            map.put("Labels.4.Label", "terrorism"); // 暴恐内容识别
            map.put("Labels.5.Label", "porn"); // 鉴黄内容识别
            map.put("Labels.6.Label", "flood"); // 灌水内容识别
            map.put("Labels.7.Label", "contraband"); // 违禁内容识别
            map.put("Labels.8.Label", "ad"); // 广告内容识别

            OpenApiRequest request = new OpenApiRequest().setBody(map);
            RuntimeOptions runtime = new RuntimeOptions();

            // 发起请求并获取结果
            Object resp = client.callApi(params, request, runtime);

            // 解析审核结果
            String responseStr = Common.toJSONString(resp);

            // 使用 hutool 解析返回的 JSON 数据
            JSONObject responseJson = JSONUtil.parseObj(responseStr);

            // 获取审核结果
            JSONObject body = responseJson.getJSONObject("body");
            JSONObject data = body.getJSONObject("Data");
            JSONArray elements = data.getJSONArray("Elements");

            // 检查所有结果，如果有任何一个结果建议为"block"，则审核不通过
            Integer status = ExamineStatusEnum.PASS.getCode();
            StringBuilder errorMessage = new StringBuilder();

            // 遍历Elements数组中的每个元素
            for (int i = 0; i < elements.size(); i++) {
                JSONObject element = elements.get(i, JSONObject.class);
                JSONArray results = element.getJSONArray("Results");

                // 遍历Results数组中的每个元素
                for (int j = 0; j < results.size(); j++) {
                    JSONObject result = results.get(j, JSONObject.class);
                    String suggestion = result.getStr("Suggestion");
                    String label = result.getStr("Label");
                    Double rate = result.getDouble("Rate");

                    // 获取违规分类描述
                    String labelDescription = getLabelDescription(label);

                    // 提取违规关键词
                    StringBuilder contextInfo = new StringBuilder();
                    JSONArray details = result.getJSONArray("Details");
                    if (details != null && !details.isEmpty()) {
                        for (int k = 0; k < details.size(); k++) {
                            JSONObject detail = details.get(k, JSONObject.class);
                            JSONArray contexts = detail.getJSONArray("Contexts");
                            if (contexts != null && !contexts.isEmpty()) {
                                for (int l = 0; l < contexts.size(); l++) {
                                    JSONObject contextObj = contexts.get(l, JSONObject.class);
                                    String context = contextObj.getStr("Context");
                                    if (context != null && !context.isEmpty()) {
                                        if (contextInfo.length() > 0) {
                                            contextInfo.append(", ");
                                        }
                                        contextInfo.append(context);
                                    }
                                }
                            }
                        }
                    }

                    // 如果建议是"block"，则审核不通过
                    if ("block".equals(suggestion)) {
                        status = ExamineStatusEnum.NO_PASS.getCode(); // 审核不通过
                        // 构建错误信息
                        errorMessage.append("检测到违规内容: ")
                                .append(labelDescription)
                                .append("(")
                                .append(label)
                                .append(")");

                        // 添加违规关键词信息
                        if (contextInfo.length() > 0) {
                            errorMessage.append(", 关键词: ")
                                    .append(contextInfo);
                        }

                        errorMessage.append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    } else if ("review".equals(suggestion)) {
                        // 如果建议是"review"，则需要人工审核
                        status = ExamineStatusEnum.WAIT.getCode(); // 需要人工审核(待审核)
                        errorMessage.append("需要人工审核: ")
                                .append(labelDescription)
                                .append("(")
                                .append(label)
                                .append(")");

                        // 添加需要人工审核的关键词信息
                        if (contextInfo.length() > 0) {
                            errorMessage.append(", 关键词: ")
                                    .append(contextInfo);
                        }

                        errorMessage.append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    }
                }
            }

            log.info("文字内容审核结果:{} ;状态:{} ; 原因:{}", elements, status, errorMessage);
            return new ImageAuditResult(status, errorMessage.toString());
        } catch (com.aliyun.tea.TeaException e) {
            log.error("文字内容审核失败:{}", e);
            // 审核服务异常,需要人工审核
            return new ImageAuditResult(2, "文字内容审核过程中发生错误: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}