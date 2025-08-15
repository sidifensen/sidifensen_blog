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
import com.sidifensen.domain.constants.ImageAuditConstants;
import com.sidifensen.domain.result.ImageAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ImageAuditUtils {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.imageaudit.endpoint}")
    private String endpoint;

    /**
     * 创建阿里云图片审核客户端
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
     * @throws Exception
     */
    private Params createApiInfo() {
        return new Params()
                // 接口名称
                .setAction("ScanImage")
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
     * 审核图片并返回详细结果
     *
     * @param imageUrl 图片URL地址
     * @return 审核结果详情
     */
    public ImageAuditResult auditImageWithDetails(String imageUrl) {
        try {
            // 对URL进行编码处理，解决URL中包含特殊字符导致无法下载的问题
            String encodedImageUrl = encodeImageUrl(imageUrl);
            
            Client client = createClient();
            Params params = createApiInfo();

            // 构造请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("Task.1.ImageURL", encodedImageUrl);
            map.put("Scene.1", "porn"); // 检测色情内容
            map.put("Scene.2", "terrorism"); // 检测暴恐内容
            map.put("Scene.3", "live"); // 检测不良场景
            map.put("Scene.4", "logo"); // Logo识别
//            map.put("Scene.5", "ad"); // 检测广告内容

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
            JSONArray results = data.getJSONArray("Results");

            // 检查所有结果，如果有任何一个结果建议为"block"，则审核不通过
            Integer status = 0;
            StringBuilder errorMessage = new StringBuilder();
            
            // 遍历Results数组中的每个元素
            for (int i = 0; i < results.size(); i++) {
                JSONObject result = results.get(i, JSONObject.class);
                JSONArray subResults = result.getJSONArray("SubResults");
                
                // 遍历SubResults数组中的每个元素
                for (int j = 0; j < subResults.size(); j++) {
                    JSONObject subResult = subResults.get(j, JSONObject.class);
                    String suggestion = subResult.getStr("Suggestion");
                    String scene = subResult.getStr("Scene");
                    String label = subResult.getStr("Label");
                    Double rate = subResult.getDouble("Rate");
                    
                    // 如果建议是"block"，则审核不通过
                    if (ImageAuditConstants.Suggestion.BLOCK.equals(suggestion)) {
                        status = 1;
                        // 构建错误信息
                        errorMessage.append(getSceneDescription(scene))
                                .append(":")
                                .append(getSceneLabelMessage(scene, label))
                                .append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    } else if (ImageAuditConstants.Suggestion.REVIEW.equals(suggestion)) {
                        // 如果建议是"review"，则需要人工审核
                        status = 2;
                        errorMessage.append(getSceneDescription(scene))
                                .append(":")
                                .append(getSceneLabelMessage(scene, label))
                                .append(", 置信度: ")
                                .append(rate)
                                .append("%; ");
                    }
                }
            }
            
            log.info("图片:{} ,审核结果:{}", imageUrl, results);
            return new ImageAuditResult(status, errorMessage.toString());
        } catch (com.aliyun.tea.TeaException e) {
            log.error("图片:{} ,审核失败:{}", imageUrl, e);
            // 审核服务异常,需要人工审核
            ImageAuditResult imageAuditResult = new ImageAuditResult(2, "图片审核过程中发生错误: " + e.getMessage());
            return imageAuditResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 对图片URL进行编码处理，解决URL中包含特殊字符导致无法下载的问题
     * 
     * @param imageUrl 原始图片URL
     * @return 编码后的图片URL
     */
    private String encodeImageUrl(String imageUrl) {
        try {
            // 如果URL中包含非ASCII字符或特殊字符，则进行编码
            if (imageUrl.matches(".*[^\\x00-\\x7F]+.*") || imageUrl.contains(" ")) {
                log.info("URL编码处理开始，使用原始URL: {}", imageUrl);
                // 解析URL并重新构建，对路径部分进行编码
                java.net.URI uri = java.net.URI.create(imageUrl);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath();
                String query = uri.getQuery();
                
                // 对路径和查询参数进行编码
                StringBuilder encodedUrl = new StringBuilder();
                encodedUrl.append(scheme).append("://").append(host);
                if (port != -1) {
                    encodedUrl.append(":").append(port);
                }
                
                // 对路径中的特殊字符进行编码
                String[] pathSegments = path.split("/");
                for (int i = 0; i < pathSegments.length; i++) {
                    if (!pathSegments[i].isEmpty()) {
                        pathSegments[i] = URLEncoder.encode(pathSegments[i], StandardCharsets.UTF_8.toString());
                    }
                }
                encodedUrl.append(String.join("/", pathSegments));
                
                // 添加查询参数（如果有的话）
                if (query != null && !query.isEmpty()) {
                    encodedUrl.append("?").append(query);
                }
                
                log.info("URL编码处理成功，使用编码后的URL: {}", encodedUrl.toString());
                return encodedUrl.toString();
            }
            return imageUrl;
        } catch (Exception e) {
            log.warn("URL编码处理失败，使用原始URL: {}", imageUrl, e);
            return imageUrl;
        }
    }

    /**
     * 根据场景和标签获取错误信息
     *
     * @param scene 场景
     * @param label 标签
     * @return 错误信息
     */
    private String getSceneLabelMessage(String scene, String label) {
        switch (scene) {
            case ImageAuditConstants.Scene.PORN:
                switch (label) {
                    case ImageAuditConstants.Label.SEXY: return ImageAuditConstants.LabelDescription.SEXY;
                    case ImageAuditConstants.Label.PORN: return ImageAuditConstants.LabelDescription.PORN;
                    default: return ImageAuditConstants.LabelDescription.PORN_EXCEPTION;
                }
            case ImageAuditConstants.Scene.TERRORISM:
                switch (label) {
                    case ImageAuditConstants.Label.BLOODY: return ImageAuditConstants.LabelDescription.BLOODY;
                    case ImageAuditConstants.Label.EXPLOSION: return ImageAuditConstants.LabelDescription.EXPLOSION;
                    case ImageAuditConstants.Label.OUTFIT: return ImageAuditConstants.LabelDescription.OUTFIT;
                    case ImageAuditConstants.Label.LOGO: return ImageAuditConstants.LabelDescription.LOGO;
                    case ImageAuditConstants.Label.WEAPON: return ImageAuditConstants.LabelDescription.WEAPON;
                    case ImageAuditConstants.Label.POLITICS: return ImageAuditConstants.LabelDescription.POLITICS;
                    case ImageAuditConstants.Label.VIOLENCE: return ImageAuditConstants.LabelDescription.VIOLENCE;
                    case ImageAuditConstants.Label.CROWD: return ImageAuditConstants.LabelDescription.CROWD;
                    case ImageAuditConstants.Label.PARADE: return ImageAuditConstants.LabelDescription.PARADE;
                    case ImageAuditConstants.Label.CARCRASH: return ImageAuditConstants.LabelDescription.CARCRASH;
                    case ImageAuditConstants.Label.FLAG: return ImageAuditConstants.LabelDescription.FLAG;
                    case ImageAuditConstants.Label.LOCATION: return ImageAuditConstants.LabelDescription.LOCATION;
                    case ImageAuditConstants.Label.DRUG: return ImageAuditConstants.LabelDescription.DRUG;
                    case ImageAuditConstants.Label.GAMBLE: return ImageAuditConstants.LabelDescription.GAMBLE;
                    case ImageAuditConstants.Label.OTHERS: return ImageAuditConstants.LabelDescription.OTHERS;
                    default: return ImageAuditConstants.LabelDescription.TERRORISM_EXCEPTION;
                }
            case ImageAuditConstants.Scene.AD:
                switch (label) {
                    case ImageAuditConstants.Label.POLITICS_TEXT: return ImageAuditConstants.LabelDescription.POLITICS_TEXT;
                    case ImageAuditConstants.Label.PORN_TEXT: return ImageAuditConstants.LabelDescription.PORN_TEXT;
                    case ImageAuditConstants.Label.ABUSE: return ImageAuditConstants.LabelDescription.ABUSE;
                    case ImageAuditConstants.Label.TERRORISM_TEXT: return ImageAuditConstants.LabelDescription.TERRORISM_TEXT;
                    case ImageAuditConstants.Label.CONTRABAND: return ImageAuditConstants.LabelDescription.CONTRABAND;
                    case ImageAuditConstants.Label.SPAM: return ImageAuditConstants.LabelDescription.SPAM;
                    case ImageAuditConstants.Label.NPX: return ImageAuditConstants.LabelDescription.NPX;
                    case ImageAuditConstants.Label.QRCODE: return ImageAuditConstants.LabelDescription.QRCODE;
                    case ImageAuditConstants.Label.PROGRAM_CODE: return ImageAuditConstants.LabelDescription.PROGRAM_CODE;
                    case ImageAuditConstants.Label.AD: return ImageAuditConstants.LabelDescription.AD;
                    default: return ImageAuditConstants.LabelDescription.AD_EXCEPTION;
                }
            case ImageAuditConstants.Scene.LIVE:
                switch (label) {
                    case ImageAuditConstants.Label.MEANINGLESS: return ImageAuditConstants.LabelDescription.MEANINGLESS;
                    case ImageAuditConstants.Label.PIP: return ImageAuditConstants.LabelDescription.PIP;
                    case ImageAuditConstants.Label.SMOKING: return ImageAuditConstants.LabelDescription.SMOKING;
                    case ImageAuditConstants.Label.DRIVELIVE: return ImageAuditConstants.LabelDescription.DRIVELIVE;
                    case ImageAuditConstants.Label.DRUG: return ImageAuditConstants.LabelDescription.DRUG;
                    case ImageAuditConstants.Label.GAMBLE: return ImageAuditConstants.LabelDescription.GAMBLE;
                    default: return ImageAuditConstants.LabelDescription.LIVE_EXCEPTION;
                }
            case ImageAuditConstants.Scene.LOGO:
                switch (label) {
                    case ImageAuditConstants.Label.TV: return ImageAuditConstants.LabelDescription.TV;
                    case ImageAuditConstants.Label.TRADEMARK: return ImageAuditConstants.LabelDescription.TRADEMARK;
                    case ImageAuditConstants.Label.NORMAL: return ImageAuditConstants.LabelDescription.NORMAL;
                    default: return ImageAuditConstants.LabelDescription.UNKNOWN;
                }
            default:
                return ImageAuditConstants.LabelDescription.UNKNOWN;
        }
    }
    
    /**
     * 获取场景描述
     *
     * @param scene 场景
     * @return 场景描述
     */
    private String getSceneDescription(String scene) {
        switch (scene) {
            case ImageAuditConstants.Scene.PORN: return ImageAuditConstants.SceneDescription.PORN;
            case ImageAuditConstants.Scene.TERRORISM: return ImageAuditConstants.SceneDescription.TERRORISM;
            case ImageAuditConstants.Scene.AD: return ImageAuditConstants.SceneDescription.AD;
            case ImageAuditConstants.Scene.LIVE: return ImageAuditConstants.SceneDescription.LIVE;
            case ImageAuditConstants.Scene.LOGO: return ImageAuditConstants.SceneDescription.LOGO;
            default: return ImageAuditConstants.SceneDescription.UNKNOWN;
        }
    }

}