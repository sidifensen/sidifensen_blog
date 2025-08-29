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
import com.sidifensen.domain.result.ImageAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
        try {
            Client client = createClient();
            Params params = createApiInfo();

            // 构造请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("Tasks.1.Content", textContent);
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
            Integer status = 0;
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

                    // 如果建议是"block"，则审核不通过
                    if ("block".equals(suggestion)) {
                        status = 1; // 审核不通过
                        // 构建错误信息
                        errorMessage.append("检测到违规内容: ")
                                .append(label)
                                .append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    } else if ("review".equals(suggestion)) {
                        // 如果建议是"review"，则需要人工审核
                        status = 2; // 需要人工审核
                        errorMessage.append("需要人工审核: ")
                                .append(label)
                                .append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    }
                }
            }

            log.info("文字内容审核结果:{}", elements);
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