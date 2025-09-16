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
import com.sidifensen.domain.result.AuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 文字内容审核工具类
 *
 * @author sidifensen
 * @since 2025-08-29
 */
@Slf4j
@Component
public class TextAuditUtils {

    // 常见的HTML标签列表，用于更精确地识别HTML标签
    private static final Set<String> COMMON_HTML_TAGS = new HashSet<>();

    static {
        String[] tags = {
                "html", "body",
                "h1", "h2", "h3", "h4", "h5", "h6",
                "p", "strong", "em", "u", "s", "code", "mark", "span",
                "ul", "ol", "li",
                "a", "blockquote", "hr",
                "table", "colgroup", "col", "tbody", "tr", "th", "td",
                "label", "input",
                "pre",
                "div", "span",
                "img"
        };
        COMMON_HTML_TAGS.addAll(List.of(tags));
    }

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.imageaudit.endpoint}")
    private String endpoint;

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
     * 审核文字内容并返回详细结果（支持超过一万字的文本拆分审核）
     *
     * @param textContent 文字内容
     * @return 审核结果详情
     */
    public AuditResult auditTextWithDetailsSplit(String textContent) {
        // 去除标签，只保留纯文本内容进行审核
        String cleanTextContent = removeHtmlTags(textContent);

        // 检查文本长度，如果不超过一万字，直接调用原方法
        if (cleanTextContent.length() <= 10000) {
            return auditTextWithDetails(textContent);
        }

        // 文本超过一万字，需要拆分审核
        List<String> textSegments = splitTextIntoSegments(cleanTextContent, 9000); // 设置为9000字，留一些余量

        // 存储所有分段的审核结果
        List<AuditResult> segmentResults = new ArrayList<>();

        // 对每个分段进行审核
        for (int i = 0; i < textSegments.size(); i++) {
            String segment = textSegments.get(i);
            log.info("正在审核第 {} 段文本，长度: {} 字", i + 1, segment.length());

            try {
                // 调用单段审核方法
                AuditResult segmentResult = auditSingleTextSegment(segment);
                segmentResults.add(segmentResult);

                // 如果某一段审核失败，立即返回失败结果
                if (segmentResult.getStatus().equals(ExamineStatusEnum.NO_PASS.getCode())) {
                    log.warn("第 {} 段文本审核不通过: {}", i + 1, segmentResult.getErrorMessage());
                    return new AuditResult(ExamineStatusEnum.NO_PASS.getCode(), "第 " + (i + 1) + " 段文本审核不通过: " + segmentResult.getErrorMessage());
                }
            } catch (Exception e) {
                log.error("第 {} 段文本审核过程中发生异常: {}", i + 1, e.getMessage());
                return new AuditResult(ExamineStatusEnum.WAIT.getCode(), "第 " + (i + 1) + " 段文本审核过程中发生错误: " + e.getMessage());
            }
        }

        // 汇总所有分段的审核结果
        return aggregateAuditResults(segmentResults);
    }

    /**
     * 将文本按指定长度拆分成多个段落
     *
     * @param text      待拆分的文本
     * @param maxLength 每段的最大长度
     * @return 拆分后的文本段落列表
     */
    private List<String> splitTextIntoSegments(String text, int maxLength) {
        List<String> segments = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return segments;
        }

        int textLength = text.length();
        int startIndex = 0;

        while (startIndex < textLength) {
            int endIndex = Math.min(startIndex + maxLength, textLength);

            // 如果不是最后一段，尝试在合适的位置断开（避免在词语中间断开）
            if (endIndex < textLength) {
                // 向前查找合适的断点（句号、换行符、空格等）
                int breakPoint = findOptimalBreakPoint(text, startIndex, endIndex);
                if (breakPoint > startIndex) {
                    endIndex = breakPoint;
                }
            }

            String segment = text.substring(startIndex, endIndex).trim();
            if (!segment.isEmpty()) {
                segments.add(segment);
            }

            startIndex = endIndex;
        }

        log.info("文本拆分完成，总长度: {} 字，拆分为 {} 段", textLength, segments.size());
        return segments;
    }

    /**
     * 寻找最佳的文本断点
     *
     * @param text       原始文本
     * @param startIndex 起始位置
     * @param endIndex   结束位置
     * @return 最佳断点位置
     */
    private int findOptimalBreakPoint(String text, int startIndex, int endIndex) {
        // 优先级：句号 > 换行符 > 感叹号/问号 > 分号/冒号 > 逗号 > 空格
        char[] breakChars = {'.', '。', '\n', '\r', '!', '！', '?', '？', ';', '；', ':', '：', ',', '，', ' ', '\t'};

        int segmentLength = endIndex - startIndex;
        // 从结束位置向前查找，寻找最近的断点字符
        for (int i = endIndex - 1; i > startIndex + segmentLength / 2; i--) { // 至少要保证有一半长度
            char currentChar = text.charAt(i);
            for (char breakChar : breakChars) {
                if (currentChar == breakChar) {
                    return i + 1; // 返回断点字符后一位
                }
            }
        }

        // 如果找不到合适的断点，就在最大长度处断开
        return endIndex;
    }

    /**
     * 对单个文本段进行审核
     *
     * @param textSegment 文本段
     * @return 审核结果
     */
    private AuditResult auditSingleTextSegment(String textSegment) {
        try {
            Client client = createClient();
            Params params = createApiInfo();

            // 构造请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("Tasks.1.Content", textSegment);
            // 检测场景
            map.put("Labels.1.Label", "spam"); // 垃圾内容识别
            map.put("Labels.2.Label", "politics"); // 敏感内容识别
            map.put("Labels.3.Label", "abuse"); // 辱骂内容识别
            map.put("Labels.4.Label", "terrorism"); // 暴恐内容识别
            map.put("Labels.5.Label", "porn"); // 鉴黄内容识别
            map.put("Labels.6.Label", "flood"); // 灌水内容识别
            map.put("Labels.7.Label", "contraband"); // 违禁内容识别
            // map.put("Labels.8.Label", "ad"); // 广告内容识别

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

            // 检查所有结果，默认为通过
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
                        status = ExamineStatusEnum.WAIT.getCode();
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
                    } else {
                        // 如果建议是"pass"，则审核通过
                        status = ExamineStatusEnum.PASS.getCode();
                    }
                }
            }

            return new AuditResult(status, errorMessage.toString());
        } catch (com.aliyun.tea.TeaException e) {
            log.error("文本段审核失败:{}", e);
            // 审核服务异常,需要人工审核
            return new AuditResult(2, "文本段审核过程中发生错误: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 汇总多个分段的审核结果
     *
     * @param segmentResults 分段审核结果列表
     * @return 汇总后的审核结果
     */
    private AuditResult aggregateAuditResults(List<AuditResult> segmentResults) {
        if (segmentResults.isEmpty()) {
            return new AuditResult(ExamineStatusEnum.PASS.getCode(), "审核通过");
        }

        // 检查是否有审核不通过的分段
        StringBuilder allMessages = new StringBuilder();
        Integer finalStatus = ExamineStatusEnum.PASS.getCode(); // 默认通过

        for (int i = 0; i < segmentResults.size(); i++) {
            AuditResult result = segmentResults.get(i);

            // 如果有任何一段不通过，整体就不通过
            if (result.getStatus().equals(ExamineStatusEnum.NO_PASS.getCode())) {
                finalStatus = ExamineStatusEnum.NO_PASS.getCode();
                if (allMessages.length() > 0) {
                    allMessages.append("; ");
                }
                allMessages.append("第").append(i + 1).append("段: ").append(result.getErrorMessage());
            } else if (result.getStatus().equals(ExamineStatusEnum.WAIT.getCode()) && finalStatus.equals(ExamineStatusEnum.PASS.getCode())) {
                // 如果没有不通过的，但有需要人工审核的，则设置为需要人工审核
                finalStatus = ExamineStatusEnum.WAIT.getCode();
                if (allMessages.length() > 0) {
                    allMessages.append("; ");
                }
                allMessages.append("第").append(i + 1).append("段: ").append(result.getErrorMessage());
            }
        }

        // 如果所有分段都通过，返回通过结果
        if (finalStatus.equals(ExamineStatusEnum.PASS.getCode())) {
            log.info("所有文本分段审核通过，共 {} 段", segmentResults.size());
            return new AuditResult(ExamineStatusEnum.PASS.getCode(), "所有文本分段审核通过（共 " + segmentResults.size() + " 段）");
        }

        // 返回汇总的审核结果
        String message = allMessages.toString();
        log.info("文本分段审核汇总结果: 状态={}, 信息={}", finalStatus, message);
        return new AuditResult(finalStatus, message);
    }

    /**
     * 审核文字内容并返回详细结果
     *
     * @param textContent 文字内容
     * @return 审核结果详情
     */
    public AuditResult auditTextWithDetails(String textContent) {
        // 去除标签，只保留纯文本内容进行审核
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
            // map.put("Labels.8.Label", "ad"); // 广告内容识别

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

            // 检查所有结果，默认为通过
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
                        status = ExamineStatusEnum.WAIT.getCode();
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
                    } else {
                        // 如果建议是"pass"，则审核通过
                        status = ExamineStatusEnum.PASS.getCode();
                    }
                }
            }

            log.info("文字内容审核结果:{} ;状态:{} ; 原因:{}", elements, status, errorMessage);
            return new AuditResult(status, errorMessage.toString());
        } catch (com.aliyun.tea.TeaException e) {
            log.error("文字内容审核失败:{}", e);
            // 审核服务异常,需要人工审核
            return new AuditResult(ExamineStatusEnum.WAIT.getCode(), "文字内容审核过程中发生错误: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String removeHtmlTags(String textContent) {

        if (textContent == null || textContent.isEmpty()) {
            return textContent;
        }

        String result = textContent;

        // 先处理HTML注释
        result = result.replaceAll("<!--.*?-->", "");

        //        // 使用正则表达式去除所有HTML标签（包括自定义属性）
        //        result = result.replaceAll("<[^>]*>", "");

        // 使用更精确的正则表达式来匹配真实的HTML标签
        // 匹配开始标签和自闭合标签，要求标签名后必须跟空白字符、>或/，确保不会误删用户输入的内容
        result = result.replaceAll("<([a-zA-Z][a-zA-Z0-9]*)(\\s[^>]*?|/?>)(</\\1>)?", "");
        // 匹配结束标签
        result = result.replaceAll("</([a-zA-Z][a-zA-Z0-9]*)\\s*>", "");

        // 处理一些特殊字符的转换
        result = result.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");

        return result.trim();
    }
}